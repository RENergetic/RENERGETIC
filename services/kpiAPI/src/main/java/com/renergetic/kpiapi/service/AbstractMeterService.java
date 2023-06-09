package com.renergetic.kpiapi.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.renergetic.kpiapi.dao.AbstractMeterDAO;
import com.renergetic.kpiapi.exception.IdAlreadyDefinedException;
import com.renergetic.kpiapi.exception.IdNoDefinedException;
import com.renergetic.kpiapi.exception.InvalidArgumentException;
import com.renergetic.kpiapi.exception.NotFoundException;
import com.renergetic.kpiapi.model.AbstractMeter;
import com.renergetic.kpiapi.model.AbstractMeterConfig;
import com.renergetic.kpiapi.model.Domain;
import com.renergetic.kpiapi.repository.AbstractMeterRepository;

@Service
public class AbstractMeterService {
	
	@Autowired
	private AbstractMeterRepository amRepo;
	
	/**
	 * Returns a map of meter names and their descriptions.
	 *
	 * @return  a map containing meter names as keys and their respective descriptions as values.
	 */
	public Map<String, String> list() {
		Map<String, String> meters = new TreeMap<>();
		for (AbstractMeter meter : AbstractMeter.values()) {
			meters.put(meter.meter, meter.description);
		}
		return meters;
	}
	
	/**
	 * Returns a list of AbstractMeterDAO instances, with optional pagination. If pagination is requested
	 * (limit parameter is not null and greater than 0), the offset and limit parameters are used to
	 * determine which elements of the resulting list should be returned.
	 *
	 * @param  offset	an Integer indicating the starting index of the paginated list, or null if no pagination is requested
	 * @param  limit	an Integer indicating the maximum number of elements to return in the list, null or 0 if no pagination is requested
	 * @return         	a List of AbstractMeterDAO instances that satisfy the pagination criteria or the entire list if no pagination is requested
	 * @throws NotFoundException if the resulting list is empty
	 */
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

	/**
	 * Retrieves an AbstractMeterDAO instance with the given name and domain.
	 *
	 * @param  name   the name of the abstract meter
	 * @param  domain the domain of the abstract meter
	 * @return        an AbstractMeterDAO instance
	 * @throws NotFoundException if an abstract meter with the given name and domain doesn't exist
	 */
	public AbstractMeterDAO get(String name, Domain domain) {
		return AbstractMeterDAO.create(amRepo.findByNameAndDomain(AbstractMeter.obtain(name), domain)
				.orElseThrow(() -> new NotFoundException("The abstract meter with name %s and domain %s isn't configured", name, domain)));
	}

	/**
	 * Creates a new AbstractMeterDAO object if it does not exist in the repository, 
	 * or throws an exception if it already exists.
	 *
	 * @param  meter  the AbstractMeterDAO object to be created
	 * @return        the newly created AbstractMeterDAO object
	 * @throws IdAlreadyDefinedException if the abstract meter with the given name and domain already exists in the repository
	 * @throws InvalidArgumentException if the given formula is not a valid mathematical formula
	 */
	public AbstractMeterDAO create(AbstractMeterDAO meter) {
		if (!validateFormula(meter.getFormula())) 
			throw new InvalidArgumentException("%s isn't a valid mathematical formula", meter.getFormula());

		if( !amRepo.existsByNameAndDomain(AbstractMeter.obtain(meter.getName()), meter.getDomain())) {
			meter.setId(null);
			return AbstractMeterDAO.create(amRepo.save(meter.mapToEntity()));
		}
		else throw new IdAlreadyDefinedException("The abstract meter with name %s and domain %s already is configured, use PUT request", meter.getName(), meter.getDomain());
	}

	/**
	 * Updates an instance of AbstractMeterDAO.
	 *
	 * @param  meter	instance of AbstractMeterDAO to update
	 * @return       	instance of AbstractMeterDAO that has been updated
	 * @throws       	IdNoDefinedException if the abstract meter with given name and domain isn't configured
	 * @throws       	InvalidArgumentException if the given formula is not a valid mathematical formula
	 */
	public AbstractMeterDAO update(AbstractMeterDAO meter) {
		if (!validateFormula(meter.getFormula())) 
			throw new InvalidArgumentException("%s isn't a valid mathematical formula", meter.getFormula());

		Optional<AbstractMeterConfig> previousConfig = amRepo.findByNameAndDomain(AbstractMeter.obtain(meter.getName()), meter.getDomain());
		if(previousConfig.isPresent()) {
			AbstractMeterConfig config = meter.mapToEntity();
			config.setId(previousConfig.get().getId());

			return AbstractMeterDAO.create(amRepo.save(config));
		}
		else throw new IdNoDefinedException("The abstract meter with name %s and domain %s isn't configured, use POST request", meter.getName(), meter.getDomain());
	}

	/**
	 * Deletes the given AbstractMeterDAO from the database if it exists, and returns the corresponding AbstractMeterDAO.
	 *
	 * @param  meter	the AbstractMeterDAO to be deleted
	 * @return         	the deleted AbstractMeterDAO
	 * @throws IdNoDefinedException	If the AbstractMeterDAO is not found in the database
	 */
	public AbstractMeterDAO delete(AbstractMeterDAO meter) {
		Optional<AbstractMeterConfig> previousConfig = amRepo.findByNameAndDomain(AbstractMeter.obtain(meter.getName()), meter.getDomain());
		if(previousConfig.isPresent()) {
			amRepo.delete(previousConfig.get());
			return AbstractMeterDAO.create(previousConfig.get());
		}
		else throw new IdNoDefinedException("The abstract meter with name %s and domain %s isn't configured", meter.getName(), meter.getDomain());
	}

	/**
	 * Validates a mathematical formula.
	 *
	 * @param  formula  string containing the formula to be validated
	 * @return          true if the formula is valid and all its parenthesis are closed, false otherwise
	 */
	public boolean validateFormula(String formula) {
		// Regex to validate a mathematical formula
		Pattern pattern = Pattern.compile("\\(*((\\d+(\\.\\d+)?)|(\\[\\d+\\]))([+*^\\/-]\\(*((\\d+(\\.\\d+)?)|(\\[\\d+\\]))\\)*)*");

		// Check if the formula close all its parenthesis
		if (pattern.matcher(formula).matches())
			return formula.chars().filter(ch -> ch == '(').count() == formula.chars().filter(ch -> ch == ')').count();
		return false;
	}
}
