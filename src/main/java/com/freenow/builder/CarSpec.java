package com.freenow.builder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.freenow.domainobject.CarDO;
import com.freenow.domainvalue.EngineType;

public class CarSpec implements Specification<CarDO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SearchCriteria criteria;

	public CarSpec(SearchCriteria param) {
		this.criteria = param;
	}
	
	@Override
	public Predicate toPredicate(Root<CarDO> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		if (criteria.getOperation().equalsIgnoreCase(">")) {
			return builder.greaterThan(root.<String> get(criteria.getKey()), criteria.getValue().toString());
		} else if (criteria.getOperation().equalsIgnoreCase("<")) {
			return builder.lessThan(root.<String> get(criteria.getKey()), criteria.getValue().toString());
		} else if (criteria.getOperation().equalsIgnoreCase("=")) {
			if (criteria.getKey().equalsIgnoreCase("engineType")) {
				return builder.equal(root.get(criteria.getKey()), EngineType.valueOf(criteria.getValue().toString()));
			} else if (root.get(criteria.getKey()).getJavaType() == String.class) {
				return builder.like(root.<String> get(criteria.getKey()), "%" + criteria.getValue() + "%");
			} else {
				return builder.equal(root.get(criteria.getKey()), criteria.getValue());
			}
		}
		return null;
	}

}
