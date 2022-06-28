package com.renergetic.backdb.service;

import com.renergetic.backdb.dao.DemandScheduleDAO;
import com.renergetic.backdb.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.backdb.exception.InvalidNonExistingIdException;
import com.renergetic.backdb.mapper.DemandRequestMapper;
import com.renergetic.backdb.model.DemandSchedule;
import com.renergetic.backdb.repository.DemandScheduleRepository;
import com.renergetic.backdb.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DemandRequestService {
    @Autowired
    DemandScheduleRepository demandScheduleRepository;
    @Autowired
    DemandRequestMapper demandRequestMapper;

    @Autowired
    UserService userService;

    public List<DemandScheduleDAO> save(List<DemandScheduleDAO> demandScheduleDAOS) throws InvalidCreationIdAlreadyDefinedException{
        LocalDateTime currentDateTime = LocalDateTime.now();

        Optional<DemandScheduleDAO> demandRequest = demandScheduleDAOS.stream().filter(x -> x.getId() != null).findAny();
        if (demandRequest.isPresent())
            throw new InvalidCreationIdAlreadyDefinedException();

        List<DemandSchedule> processed = demandScheduleDAOS.stream().map(d -> {
            d.setUpdate(currentDateTime);
            return d.mapToEntity();
        }).collect(Collectors.toList());

        return demandScheduleRepository.saveAll(processed).stream().map(DemandScheduleDAO::create).collect(Collectors.toList());
    }

    public DemandScheduleDAO save(DemandScheduleDAO demandScheduleDAO) throws InvalidCreationIdAlreadyDefinedException {
        demandScheduleDAO.setUpdate(LocalDateTime.now());
        if (demandScheduleDAO.getId() != null)
            throw new InvalidCreationIdAlreadyDefinedException();

        return DemandScheduleDAO.create(demandScheduleRepository.save(demandScheduleDAO.mapToEntity()));
    }

    public DemandScheduleDAO update(DemandScheduleDAO demandRequestDAO) throws InvalidNonExistingIdException {
        demandRequestDAO.setUpdate(LocalDateTime.now());
        if(!demandScheduleRepository.existsById(demandRequestDAO.getId()))
            throw new InvalidNonExistingIdException();

        return DemandScheduleDAO.create(demandScheduleRepository.save(demandRequestDAO.mapToEntity()));
    }

    public List<DemandScheduleDAO> getAll(long offset, int limit){
        return demandScheduleRepository.findAll(new OffSetPaging(offset, limit)).stream().map(DemandScheduleDAO::create).collect(Collectors.toList());
    }

    public DemandScheduleDAO getById(Long id) throws InvalidNonExistingIdException {
        Optional<DemandSchedule> demandRequest = demandScheduleRepository.findById(id);
        if (demandRequest.isEmpty())
            throw new InvalidNonExistingIdException();

        return DemandScheduleDAO.create(demandRequest.get());
    }

    public List<DemandScheduleDAO> getByUserId(Long userId, long offset, int limit) throws InvalidNonExistingIdException {
        List<DemandSchedule> demandRequest = demandScheduleRepository.findByUserId(userId, LocalDateTime.now(), offset, limit);

        return demandRequest.stream().map(DemandScheduleDAO::create).collect(Collectors.toList());
    }

    public DemandScheduleDAO getByAssetIdAndActual(Long assetId){
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<DemandSchedule> demandRequest = demandScheduleRepository.findByAssetIdAndDemandStartLessThanEqualAndDemandStopGreaterThanEqual(assetId, localDateTime, localDateTime);
        if (demandRequest.isEmpty())
            return null;

        return DemandScheduleDAO.create(demandRequest.get());
    }
}