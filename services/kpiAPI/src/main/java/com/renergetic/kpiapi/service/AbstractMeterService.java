package com.renergetic.kpiapi.service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.renergetic.kpiapi.dao.AbstractMeterIdentifier;
import com.renergetic.kpiapi.dao.AbstractMeterTypeDAO;
import com.renergetic.kpiapi.dao.MeasurementDAO;
import com.renergetic.kpiapi.model.Measurement;
import com.renergetic.kpiapi.repository.MeasurementRepository;
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

    @Autowired
    MeasurementRepository measurementRepository;

    /**
     * Returns a map of meter names and their descriptions.
     *
     * @return a map containing meter names as keys and their respective descriptions as values.
     */
    public Map<String, String> map() {
        Map<String, String> meters = new TreeMap<>();
        for (AbstractMeter meter : AbstractMeter.values()) {
            meters.put(meter.meterLabel, meter.description);
        }
        return meters;
    }

    /**
     * Returns a map of meter names and their descriptions.
     *
     * @return a map containing meter names as keys and their respective descriptions as values.
     */
    public List<AbstractMeterTypeDAO> list() {
        return Arrays.stream(AbstractMeter.values()).map(AbstractMeterTypeDAO::new).toList();
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
        AbstractMeterConfig byNameAndDomain = amRepo.findByNameAndDomain(AbstractMeter.obtain(name), domain)
                .orElseThrow(() -> new NotFoundException("The abstract meter with name %s and domain %s isn't configured", name, domain));
        return AbstractMeterDAO.create(byNameAndDomain,byNameAndDomain.getMeasurement()                );
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

        if (!calculator.validateFormula(meter.getFormula()))
            throw new InvalidArgumentException("%s isn't a valid mathematical formula", meter.getFormula());

        if (meter.getCondition() != null && calculator.validateCondition(meter.getCondition()) == null)
            throw new InvalidArgumentException("%s isn't a valid condition", meter.getCondition());

        if (!amRepo.existsByNameAndDomain(AbstractMeter.obtain(meter.getName()), meter.getDomain())) {
            meter.setId(null);
            var entity= meter.mapToEntity();
            if (meter.getMeasurement() != null) {
//check if exists
               entity.setMeasurement(measurementRepository.findById(meter.getMeasurement().getId()).orElseThrow());
            } else {
//             infer measurement
                var m = this.findMeasurement(meter);
                entity.setMeasurement(m);
            }

            var newMeter = AbstractMeterDAO.create(amRepo.save(entity),entity.getMeasurement());
            if (newMeter.getDomain() != Domain.none)
                this.updateAllDomainMeter(newMeter, false);
            return newMeter;
        } else
            throw new IdAlreadyDefinedException("The abstract meter with name %s and domain %s already is configured, use PUT request", meter.getName(), meter.getDomain());
    }

    private Measurement findMeasurement(AbstractMeterDAO meter) {
        var m = measurementRepository.inferMeasurement(meter.getName().toLowerCase(),
                "abstract_meter", meter.getDomain().name(), null, null, null);
        if (m.size() == 1) {
            return m.get(0);
        }
        return null;
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
            if (meter.getMeasurement() != null) {
//check if measurement exists
                var measurement = measurementRepository.findById(meter.getMeasurement().getId()).orElseThrow();
                config.setMeasurement(measurement);
            } else
                config.setMeasurement(null);
            config.setId(previousConfig.get().getId());
            AbstractMeterDAO updatedMeter = AbstractMeterDAO.create(amRepo.save(config),config.getMeasurement());
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
                updateAllDomainMeter(AbstractMeterDAO.create(previousConfig.get(),null), true);
            amRepo.deleteByNameAndDomain(meter, domain);
            return AbstractMeterDAO.create(previousConfig.get(),null);
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
                updateAllDomainMeter(AbstractMeterDAO.create(prev,null), true);
            amRepo.delete(prev);
            return AbstractMeterDAO.create(previousConfig.get(),null);
        } else
            throw new IdNoDefinedException("The abstract meter with name %s and domain %s isn't configured", meter.getName(), meter.getDomain());
    }

    private void updateAllDomainMeter(AbstractMeterDAO meter, boolean delete) {
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
            var measurement = this.findMeasurement(meter);
            if (measurement != null)
                meter.setMeasurement(MeasurementDAO.create(measurement));

            if (meter.getCondition() != null && !meter.getCondition().isEmpty())
                allDomainMeter.setCondition(meter.getCondition());
            else if (otherMeter != null && otherMeter.getCondition() != null && !otherMeter.getCondition().isEmpty()) {
                allDomainMeter.setCondition(otherMeter.getCondition());
            }
            allDomainMeter.setName(AbstractMeter.obtain(meter.getName()));
        }
        allDomainMeter.setFormula(formula);
        AbstractMeterDAO.create(amRepo.save(allDomainMeter),allDomainMeter.getMeasurement());
    }

    private void checkDomain(AbstractMeterDAO meter) {
        if (!(meter.getDomain() == Domain.electricity || meter.getDomain() == Domain.heat)) {
            throw new InvalidArgumentException("Invalid meter domain");
        }
    }

    public List<AbstractMeterIdentifier> getNotConfigured() {
        return amRepo.listNotConfiguredMeters();
    }
}
