package com.freenow.builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.freenow.domainobject.CarDO;

public class CarSpecBuilder {
	private final List<SearchCriteria> params;

	public CarSpecBuilder() {
	    this.params = new ArrayList<>();
	}

	public CarSpecBuilder with(String key, String operation, Object value) {
		params.add(new SearchCriteria(key, operation, value));
		return this;
	}

	public Specification<CarDO> build() {
		if (params.size() == 0) {
			return null;
		}

		List<Specification<CarDO>> specs = new ArrayList<Specification<CarDO>>();
		for (SearchCriteria param : params) {
			specs.add(new CarSpec(param));
		}

		Specification<CarDO> result = specs.get(0);
		for (int i = 1; i < specs.size(); i++) {
			result = Specification.where(result).and(specs.get(i));
		}
		return result;
	}
}
