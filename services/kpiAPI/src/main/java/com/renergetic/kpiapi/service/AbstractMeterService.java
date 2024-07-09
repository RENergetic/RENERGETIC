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
import com.renergetic.kpiapi.service.utils.MathCalculator;
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

    @Autowired
    private MathCalculator calculator;

    /**
     * Returns a map of meter names and their descriptions.
     *
     * @return a map containing meter names as keys and their respective descriptions as values.
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
     * @param offset an Integer indicating the starting index of the paginated list, or null if no pagination is requested
     * @param limit  an Integer indicating the maximum number of elements to return in the list, null or 0 if no pagination is requested
     * @return a List of AbstractMeterDAO instances that satisfy the pagination criteria or the entire list if no pagination is requested
     * @throws NotFoundException if the resulting list is empty
     */
    public List<AbstractMeterDAO> getAll(Integer offset, Integer limit) {
        Stream<AbstractMeterConfig> stream;
        if (limit != null && limit > 0) {
            offset = offset == null ? 0 : offset;
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
     * @param name   the name of the abstract meter
     * @param domain the domain of the abstract meter
     * @return an AbstractMeterDAO instance
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
     * @param meter the AbstractMeterDAO object to be created
     * @return the newly created AbstractMeterDAO object
     * @throws IdAlreadyDefinedException if the abstract meter with the given name and domain already exists in the repository
     * @throws InvalidArgumentException  if the given formula is not a valid mathematical formula
     */
    public AbstractMeterDAO create(AbstractMeterDAO meter) {
        this.checkDomain(meter);
        if (!calculator.validateFormula(meter.getFormula()))
            throw new InvalidArgumentException("%s isn't a valid mathematical formula", meter.getFormula());

        if (meter.getCondition() != null && calculator.validateCondition(meter.getCondition()) == null)
            throw new InvalidArgumentException("%s isn't a valid condition", meter.getCondition());

        if (!amRepo.existsByNameAndDomain(AbstractMeter.obtain(meter.getName()), meter.getDomain())) {
            meter.setId(null);
            var newMeter = AbstractMeterDAO.create(amRepo.save(meter.mapToEntity()));
            this.updateAllDomainMeter(newMeter, false);
            return newMeter;
        } else
            throw new IdAlreadyDefinedException("The abstract meter with name %s and domain %s already is configured, use PUT request", meter.getName(), meter.getDomain());
    }


    /**
     * Updates an instance of AbstractMeterDAO.
     *
     * @param meter instance of AbstractMeterDAO to update
     * @return instance of AbstractMeterDAO that has been updated
     * @throws IdNoDefinedException     if the abstract meter with given name and domain isn't configured
     * @throws InvalidArgumentException if the given formula is not a valid mathematical formula
     */
    public AbstractMeterDAO update(AbstractMeterDAO meter) {

        if (!calculator.validateFormula(meter.getFormula()))
            throw new InvalidArgumentException("%s isn't a valid mathematical formula", meter.getFormula());

        if (meter.getCondition() != null && calculator.validateCondition(meter.getCondition()) == null)
            throw new InvalidArgumentException("%s isn't a valid condition", meter.getCondition());

        Optional<AbstractMeterConfig> previousConfig = amRepo.findByNameAndDomain(AbstractMeter.obtain(meter.getName()), meter.getDomain());
        if (previousConfig.isPresent()) {
            AbstractMeterConfig config = meter.mapToEntity();
            config.setId(previousConfig.get().getId());
            AbstractMeterDAO updatedMeter = AbstractMeterDAO.create(amRepo.save(config));
            if (meter.getDomain() != Domain.none) {
                this.updateAllDomainMeter(updatedMeter, false);
            }
            return updatedMeter;
        } else
            throw new IdNoDefinedException("The abstract meter with name %s and domain %s isn't configured, use POST request", meter.getName(), meter.getDomain());
    }

    /**
     * Deletes an AbstractMeterDAO object given its name and domain.
     *
     * @param name   the name of the AbstractMeterDAO object to delete
     * @param domain the domain of the AbstractMeterDAO object to delete
     * @return an AbstractMeterDAO object representing the deleted object's previous configuration
     * @throws IdNoDefinedException if the object with the given name and domain is not configured
     */
    public AbstractMeterDAO delete(String name, Domain domain) {
        AbstractMeter meter = AbstractMeter.obtain(name);

        Optional<AbstractMeterConfig> previousConfig = amRepo.findByNameAndDomain(meter, domain);
        if (previousConfig.isPresent()) {
            if (domain != Domain.none)
                updateAllDomainMeter(AbstractMeterDAO.create(previousConfig.get()), true);
            amRepo.deleteByNameAndDomain(meter, domain);
            return AbstractMeterDAO.create(previousConfig.get());
        } else
            throw new IdNoDefinedException("The abstract meter with name %s and domain %s isn't configured", meter, domain);
    }

    /**
     * Deletes the given AbstractMeterDAO from the database if it exists, and returns the corresponding AbstractMeterDAO.
     *
     * @param meter the AbstractMeterDAO to be deleted
     * @return the deleted AbstractMeterDAO
     * @throws IdNoDefinedException If the AbstractMeterDAO is not found in the database
     */
    public AbstractMeterDAO delete(AbstractMeterDAO meter) {
        Optional<AbstractMeterConfig> previousConfig = amRepo.findByNameAndDomain(AbstractMeter.obtain(meter.getName()), meter.getDomain());
        if (previousConfig.isPresent()) {
            var prev = previousConfig.get();
            if (prev.getDomain() != Domain.none)
                updateAllDomainMeter(AbstractMeterDAO.create(prev), true);
            amRepo.delete(prev);
            return AbstractMeterDAO.create(previousConfig.get());
        } else
            throw new IdNoDefinedException("The abstract meter with name %s and domain %s isn't configured", meter.getName(), meter.getDomain());
    }

    private AbstractMeterDAO updateAllDomainMeter(AbstractMeterDAO meter, boolean delete) {
        String formula = delete ? "0" : meter.getFormula();
        AbstractMeterConfig otherMeter;
        if (meter.getDomain() == Domain.none) {
            throw new IllegalArgumentException("Only heat and electricity domains are possible");
        }
        if (meter.getDomain() == Domain.electricity) {
            otherMeter = amRepo.findByNameAndDomain(AbstractMeter.obtain(meter.getName()), Domain.heat).orElse(null);
        } else {
            otherMeter = amRepo.findByNameAndDomain(AbstractMeter.obtain(meter.getName()), Domain.electricity).orElse(null);
        }
        if (otherMeter != null) {
            formula += "+" + otherMeter.getFormula();
        }
        var m = amRepo.findByNameAndDomain(AbstractMeter.obtain(meter.getName()), Domain.none);
        AbstractMeterConfig allDomainMeter;
        if (m.isPresent()) {
            allDomainMeter = m.get();
        } else {
            allDomainMeter = new AbstractMeterConfig();
            allDomainMeter.setDomain(Domain.none);

            if (meter.getType() != null )
                allDomainMeter.setType(meter.getType());
            else if (otherMeter != null && otherMeter.getType() != null ) {
                allDomainMeter.setType(otherMeter.getType());
            }
            if (meter.getCondition() != null && !meter.getCondition().isEmpty())
                allDomainMeter.setCondition(meter.getCondition());
            else if (otherMeter != null && otherMeter.getCondition() != null && !otherMeter.getCondition().isEmpty()) {
                allDomainMeter.setCondition(otherMeter.getCondition());
            }
            allDomainMeter.setName(AbstractMeter.obtain(meter.getName()));
        }
        allDomainMeter.setFormula(formula);
        return AbstractMeterDAO.create(amRepo.save(allDomainMeter));
    }

    private void checkDomain(AbstractMeterDAO meter) {
        if (!(meter.getDomain() == Domain.electricity || meter.getDomain() == Domain.heat)) {
            throw new InvalidArgumentException("Invalid meter domain");
        }
    }
}
