package com.freenow.controller.mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.freenow.datatransferobject.CarDTO;
import com.freenow.domainobject.CarDO;

/**
 * 
 * @author shashank
 *
 */
public class CarMapper {
	public static CarDO makeCarDO(CarDTO carDTO)
    {
        return new CarDO(carDTO.getLicensePlate(),carDTO.getSeatCount(),carDTO.getEngineType(),carDTO.getManufacturer());
    }


    public static CarDTO makeCarDTO(CarDO carDO)
    {
        CarDTO.CarDTOBuilder carDTOBuilder = CarDTO.newBuilder()
            .setId(carDO.getId())
            .setEngineType(carDO.getEngineType())
            .setLicensePlate(carDO.getLicensePlate())
            .setManufacturer(carDO.getManufacturer())
            .setSeatCount(carDO.getSeatCount());

        return carDTOBuilder.createCarDTO();
    }


    public static List<CarDTO> makeCarDTOList(Collection<CarDO> cars)
    {
        return cars.stream()
            .map(CarMapper::makeCarDTO)
            .collect(Collectors.toList());
    }

}
