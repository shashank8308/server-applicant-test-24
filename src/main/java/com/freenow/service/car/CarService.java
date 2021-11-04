package com.freenow.service.car;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.freenow.domainobject.CarDO;
import com.freenow.domainvalue.CarStatus;
import com.freenow.exception.ConstraintsViolationException;
import com.freenow.exception.EntityNotFoundException;

public interface CarService {
	CarDO find(Long carId) throws EntityNotFoundException;

	CarDO create(CarDO carDO) throws ConstraintsViolationException;

	void delete(Long carId) throws EntityNotFoundException;

	List<CarDO> find(CarStatus carStatus);

	void updateRating(long carId, BigDecimal rating) throws EntityNotFoundException;

	List<CarDO> findAll(Specification<CarDO> spec);


}
