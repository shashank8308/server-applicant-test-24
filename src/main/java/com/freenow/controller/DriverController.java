package com.freenow.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.freenow.builder.CarSpecBuilder;
import com.freenow.controller.mapper.DriverMapper;
import com.freenow.datatransferobject.DriverDTO;
import com.freenow.domainobject.CarDO;
import com.freenow.domainobject.DriverDO;
import com.freenow.domainvalue.CarStatus;
import com.freenow.domainvalue.OnlineStatus;
import com.freenow.exception.CarAlreadyInUseException;
import com.freenow.exception.ConstraintsViolationException;
import com.freenow.exception.EntityNotFoundException;
import com.freenow.exception.ProhibitedOperationException;
import com.freenow.service.car.CarService;
import com.freenow.service.driver.DriverService;

/**
 * All operations with a driver will be routed by this controller.
 * <p/>
 */
@RestController
@RequestMapping("v1/drivers")
//@Secured("ROLE_ADMIN")
public class DriverController
{

    private final DriverService driverService;
    private final CarService carService;


    @Autowired
    public DriverController(final DriverService driverService,final CarService carService)
    {
        this.driverService = driverService;
        this.carService = carService;
    }



    @GetMapping("/{driverId}")
    public DriverDTO getDriver(@PathVariable long driverId) throws EntityNotFoundException
    {
        return DriverMapper.makeDriverDTO(driverService.find(driverId));
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverDTO createDriver(@Valid @RequestBody DriverDTO driverDTO) throws ConstraintsViolationException
    {
        DriverDO driverDO = DriverMapper.makeDriverDO(driverDTO);
        return DriverMapper.makeDriverDTO(driverService.create(driverDO));
    }


    @DeleteMapping("/{driverId}")
    public void deleteDriver(@PathVariable long driverId) throws EntityNotFoundException
    {
        driverService.delete(driverId);
    }


    @PutMapping("/{driverId}")
    public void updateLocation(
        @PathVariable long driverId, @RequestParam double longitude, @RequestParam double latitude)
        throws EntityNotFoundException
    {
        driverService.updateLocation(driverId, longitude, latitude);
    }


    @GetMapping
    public List<DriverDTO> findDrivers(@RequestParam OnlineStatus onlineStatus)
    {
        return DriverMapper.makeDriverDTOList(driverService.find(onlineStatus));
    }
    
    
  //Driver select/deselect a Car
    @PatchMapping("/{driverId}")
    public ResponseEntity<DriverDTO> selectDeselectCarFromRepo(
        @Valid @PathVariable long driverId, @RequestParam long carId, @RequestParam CarStatus action)
        throws ConstraintsViolationException, EntityNotFoundException, CarAlreadyInUseException, ProhibitedOperationException
    {
        DriverDO driverDO=driverService.find(driverId);
        CarDO carDO=carService.find(carId);
        if(action==CarStatus.MAP)
        	driverDO=driverService.mapCar(driverDO, carDO);
        else 
        	driverDO=driverService.unMapCar(driverDO, carDO);
		return new ResponseEntity<>(DriverMapper.makeDriverDTO(driverDO),HttpStatus.ACCEPTED);
    }
    
    
  //Driver based on car Attributes
    @GetMapping("/cars")
    @ResponseBody
    public Set<DriverDTO> findDriversByCar(@RequestParam(value = "search", required = false) String search)
        throws ConstraintsViolationException, EntityNotFoundException
    {
    	CarSpecBuilder builder = new CarSpecBuilder();
    	List<DriverDTO> driverDTOList = new ArrayList<>();
        if (search != null) {
            Pattern pattern = Pattern.compile("(\\w+?)(=|<|>)(\\w+?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
            Specification<CarDO> spec= builder.build();
            List<CarDO> carDOList = carService.findAll(spec);
            
            for(CarDO carDO : carDOList){
            	DriverDO driverDO = driverService.findByCar(carDO.getId());
	            	if(driverDO != null){
	            		driverDTOList.add(DriverMapper.makeDriverDTO(driverDO));
	            	}
            }
        }
        
        return new TreeSet<>(driverDTOList);
    }
}
