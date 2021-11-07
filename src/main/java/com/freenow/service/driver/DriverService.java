package com.freenow.service.driver;

import java.util.List;

import com.freenow.domainobject.CarDO;
import com.freenow.domainobject.DriverDO;
import com.freenow.domainvalue.OnlineStatus;
import com.freenow.exception.CarAlreadyInUseException;
import com.freenow.exception.ConstraintsViolationException;
import com.freenow.exception.EntityNotFoundException;
import com.freenow.exception.ProhibitedOperationException;

/**
 * 
 * @author shashank
 *
 */
public interface DriverService
{

    DriverDO find(Long driverId) throws EntityNotFoundException;

    DriverDO create(DriverDO driverDO) throws ConstraintsViolationException;

    void delete(Long driverId) throws EntityNotFoundException;

    void updateLocation(long driverId, double longitude, double latitude) throws EntityNotFoundException;

    List<DriverDO> find(OnlineStatus onlineStatus);
    
    public DriverDO mapCar(DriverDO driverDO, CarDO carDO) throws CarAlreadyInUseException, ProhibitedOperationException;

	public DriverDO unMapCar(DriverDO driverDO, CarDO carDO) throws ProhibitedOperationException;

	public DriverDO findByCar(Long carID);

}
