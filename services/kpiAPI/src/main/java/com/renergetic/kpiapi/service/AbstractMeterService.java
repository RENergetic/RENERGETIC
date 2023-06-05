package com.renergetic.kpiapi.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.renergetic.kpiapi.dao.AbstractMeterDAO;
import com.renergetic.kpiapi.exception.IdAlreadyDefinedException;
import com.renergetic.kpiapi.exception.IdNoDefinedException;
import com.renergetic.kpiapi.exception.NotFoundException;
import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.repository.AbstractMeterRepository;

@Service
public class AbstractMeterService {
	
	@Autowired
	private AbstractMeterRepository amRepo;
	
	public Map<String, String> list() {
		Map<String, String> meters = new TreeMap<>();
		for (AbstractMeter meter : AbstractMeter.values()) {
			meters.put(meter.meter, meter.description);
		}
		return meters;
	}
	
	public List<AbstractMeterDAO> getAll(Integer offset, Integer limit) {
		Stream<AbstractMeterConfig> stream;
		if (limit != null && limit > 0) {
			offset = offset == null? 0 : offset;
			stream = amRepo.findAll(PageRequest.of(offset, limit)).stream();
		} else {
			stream = amRepo.findAll().stream();
		}
		List<AbstractMeterDAO> ret = stream
				.map(AbstractMeterDAO::create)
				.collect(Collectors.toList());
		
		if (!ret.isEmpty()) {
			return ret;
		} else throw new NotFoundException("There aren't abstract meter configured yet"); 
	}

	public AbstractMeterDAO get(String name, Domain domain) {
		return AbstractMeterDAO.create(amRepo.findByNameAndDomain(AbstractMeter.get(name), domain)
				.orElseThrow(() -> new NotFoundException("The abstract meter with name %s and domain %s isn't configured", name, domain)));
	}

	public AbstractMeterDAO create(AbstractMeterDAO meter) {
		if( !amRepo.existsByNameAndDomain(AbstractMeter.get(meter.getName()), meter.getDomain())) {
			meter.setId(null);
			return AbstractMeterDAO.create(amRepo.save(meter.mapToEntity()));
		}
		else throw new IdAlreadyDefinedException("The abstract meter with name %s and domain %s already is configured, use PUT request", meter.getName(), meter.getDomain());
	}

	public AbstractMeterDAO update(AbstractMeterDAO meter) {
		Optional<AbstractMeterConfig> previousConfig = amRepo.findByNameAndDomain(AbstractMeter.get(meter.getName()), meter.getDomain());
		if(previousConfig.isPresent()) {
			AbstractMeterConfig config = meter.mapToEntity();
			config.setId(previousConfig.get().getId());

			return AbstractMeterDAO.create(amRepo.save(config));
		}
		else throw new IdNoDefinedException("The abstract meter with name %s and domain %s isn't configured, use POST request", meter.getName(), meter.getDomain());
	}

	public AbstractMeterDAO delete(AbstractMeterDAO meter) {
		Optional<AbstractMeterConfig> previousConfig = amRepo.findByNameAndDomain(AbstractMeter.get(meter.getName()), meter.getDomain());
		if(previousConfig.isPresent()) {
			amRepo.delete(previousConfig.get());
			return AbstractMeterDAO.create(previousConfig.get());
		}
		else throw new IdNoDefinedException("The abstract meter with name %s and domain %s isn't configured", meter.getName(), meter.getDomain());
	}
}
