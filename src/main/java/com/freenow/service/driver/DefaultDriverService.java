package com.freenow.service.driver;

import com.freenow.dataaccessobject.DriverRepository;
import com.freenow.domainobject.CarDO;
import com.freenow.domainobject.DriverDO;
import com.freenow.domainvalue.CarStatus;
import com.freenow.domainvalue.GeoCoordinate;
import com.freenow.domainvalue.OnlineStatus;
import com.freenow.exception.CarAlreadyInUseException;
import com.freenow.exception.ConstraintsViolationException;
import com.freenow.exception.EntityNotFoundException;
import com.freenow.exception.ErrorCode;
import com.freenow.exception.ProhibitedOperationException;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author shashank
 *
 */
/**
 * Service to encapsulate the link between DAO and controller and to have business logic for some driver specific things.
 * <p/>
 */
@Service
public class DefaultDriverService implements DriverService
{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDriverService.class);

    private final DriverRepository driverRepository;
    private static final String MESSAGE="Could not map car with id ";


    public DefaultDriverService(final DriverRepository driverRepository)
    {
        this.driverRepository = driverRepository;
    }


    /**
     * Selects a driver by id.
     *
     * @param driverId
     * @return found driver
     * @throws EntityNotFoundException if no driver with the given id was found.
     */
    @Override
    public DriverDO find(Long driverId) throws EntityNotFoundException
    {
        return findDriverChecked(driverId);
    }


    /**
     * Creates a new driver.
     *
     * @param driverDO
     * @return
     * @throws ConstraintsViolationException if a driver already exists with the given username, ... .
     */
    @Override
    public DriverDO create(DriverDO driverDO) throws ConstraintsViolationException
    {
        DriverDO driver;
        try
        {
            driver = driverRepository.save(driverDO);
        }
        catch (DataIntegrityViolationException e)
        {
            LOG.warn("ConstraintsViolationException while creating a driver: {}", driverDO, e);
            throw new ConstraintsViolationException(e.getMessage());
        }
        return driver;
    }


    /**
     * Deletes an existing driver by id.
     *
     * @param driverId
     * @throws EntityNotFoundException if no driver with the given id was found.
     */
    @Override
    @Transactional
    public void delete(Long driverId) throws EntityNotFoundException
    {
        DriverDO driverDO = findDriverChecked(driverId);
        driverDO.setDeleted(true);
    }


    /**
     * Update the location for a driver.
     *
     * @param driverId
     * @param longitude
     * @param latitude
     * @throws EntityNotFoundException
     */
    @Override
    @Transactional
    public void updateLocation(long driverId, double longitude, double latitude) throws EntityNotFoundException
    {
        DriverDO driverDO = findDriverChecked(driverId);
        driverDO.setCoordinate(new GeoCoordinate(latitude, longitude));
    }


    /**
     * Find all drivers by online state.
     *
     * @param onlineStatus
     */
    @Override
    public List<DriverDO> find(OnlineStatus onlineStatus)
    {
        return driverRepository.findByOnlineStatus(onlineStatus);
    }


    private DriverDO findDriverChecked(Long driverId) throws EntityNotFoundException
    {
        return driverRepository.findById(driverId)
            .orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: " + driverId));
    }


	@Override
	public DriverDO mapCar(DriverDO driverDO, CarDO carDO)
			throws CarAlreadyInUseException, ProhibitedOperationException {
		
		if(driverDO.getOnlineStatus()==OnlineStatus.OFFLINE){
			throw new ProhibitedOperationException("Please be online in order to map a car.",ErrorCode.OFFLINEDRIVER);
		}else if(carDO.getCarStatus()==CarStatus.MAP){
			throw new CarAlreadyInUseException(MESSAGE+carDO.getId()+", Already in Use.");
		}else{
			driverDO.setCarDO(carDO);
			carDO.setCarStatus(CarStatus.MAP);
		}
	return driverDO;
}


	@Override
	public DriverDO unMapCar(DriverDO driverDO, CarDO carDO) throws ProhibitedOperationException {
		
		if(driverDO.getCarDO()!=null && driverDO.getCarDO().getId()==carDO.getId()){
			driverDO.setCarDO(null);
			carDO.setCarStatus(CarStatus.UNMAP);
		}else{
			throw new ProhibitedOperationException("Car with id-"+carDO.getId()+", Not mapped to you.",ErrorCode.NOTMAPPED);
		}
		return driverDO;
	}


	@Override
	public DriverDO findByCar(Long carID) {
		// TODO Auto-generated method stub
		return driverRepository.findByAndCarDO_Id(carID);
	}

}
