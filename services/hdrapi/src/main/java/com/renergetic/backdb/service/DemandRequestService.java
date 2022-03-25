package com.renergetic.backdb.service;

import com.renergetic.backdb.dao.DemandRequestDAO;
import com.renergetic.backdb.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.backdb.exception.InvalidNonExistingIdException;
import com.renergetic.backdb.mapper.DemandRequestMapper;
import com.renergetic.backdb.model.DemandRequest;
import com.renergetic.backdb.repository.DemandRequestRepository;
import com.renergetic.backdb.service.utils.OffSetPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DemandRequestService {
    @Autowired
    DemandRequestRepository demandRequestRepository;
    @Autowired
    DemandRequestMapper demandRequestMapper;

    @Autowired
    UserService userService;

    public List<DemandRequestDAO> save(List<DemandRequestDAO> demandRequestDAOs) throws InvalidCreationIdAlreadyDefinedException{
        LocalDateTime currentDateTime = LocalDateTime.now();

        Optional<DemandRequestDAO> demandRequest = demandRequestDAOs.stream().filter(x -> x.getId() != null).findAny();
        if (demandRequest.isPresent())
            throw new InvalidCreationIdAlreadyDefinedException();

        List<DemandRequest> processed = demandRequestDAOs.stream().map(d -> {
            d.setUpdateDate(currentDateTime);
            return demandRequestMapper.toEntity(d);
        }).collect(Collectors.toList());

        return demandRequestRepository.saveAll(processed).stream().map(demandRequestMapper::toDTO).collect(Collectors.toList());
    }

    public DemandRequestDAO save(DemandRequestDAO demandRequestDAO) throws InvalidCreationIdAlreadyDefinedException {
        demandRequestDAO.setUpdateDate(LocalDateTime.now());
        if (demandRequestDAO.getId() != null)
            throw new InvalidCreationIdAlreadyDefinedException();

        return demandRequestMapper.toDTO(demandRequestRepository.save(demandRequestMapper.toEntity(demandRequestDAO)));
    }

    public DemandRequestDAO update(DemandRequestDAO demandRequestDAO) throws InvalidNonExistingIdException {
        demandRequestDAO.setUpdateDate(LocalDateTime.now());
        if(!demandRequestRepository.existsById(demandRequestDAO.getId()))
            throw new InvalidNonExistingIdException();

        return demandRequestMapper.toDTO(demandRequestRepository.save(demandRequestMapper.toEntity(demandRequestDAO)));
    }

    public List<DemandRequestDAO> getAll(long offset, int limit){
        return demandRequestRepository.findAll(new OffSetPaging(offset, limit)).stream().map(demandRequestMapper::toDTO).collect(Collectors.toList());
    }

    public DemandRequestDAO getById(Long id) throws InvalidNonExistingIdException {
        Optional<DemandRequest> demandRequest = demandRequestRepository.findById(id);
        if (demandRequest.isEmpty())
            throw new InvalidNonExistingIdException();

        return demandRequestMapper.toDTO(demandRequest.get());
    }

    public List<DemandRequestDAO> getByUserId(Long userId, long offset, int limit) throws InvalidNonExistingIdException {
        List<DemandRequest> demandRequest = demandRequestRepository.findByUserId(userId, LocalDateTime.now(), offset, limit);

        return demandRequest.stream().map(demandRequestMapper::toDTO).collect(Collectors.toList());
    }

    public DemandRequestDAO getByAssetIdAndActual(Long assetId){
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<DemandRequest> demandRequest = demandRequestRepository.findByAssetIdAndStartLessThanEqualAndStopGreaterThanEqual(assetId, localDateTime, localDateTime);
        if (demandRequest.isEmpty())
            return null;

        return demandRequestMapper.toDTO(demandRequest.get());
    }
}