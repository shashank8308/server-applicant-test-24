package com.freenow.dataaccessobject;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.freenow.domainobject.CarDO;
import com.freenow.domainvalue.CarStatus;

/**
 * 
 * @author shashank
 *
 */
public interface CarRepository extends JpaRepository<CarDO, Long>, JpaSpecificationExecutor<CarDO> {

	List<CarDO> findByCarStatus(CarStatus carStatus);
}
