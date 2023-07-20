package com.renergetic.hdrapi.service;

import com.renergetic.common.utilities.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renergetic.common.dao.DemandDefinitionDAO;
import com.renergetic.common.dao.DemandScheduleDAO;
import com.renergetic.common.exception.InvalidCreationIdAlreadyDefinedException;
import com.renergetic.common.exception.InvalidNonExistingIdException;
import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.DemandDefinition;
import com.renergetic.common.model.DemandSchedule;
import com.renergetic.common.repository.DemandDefinitionRepository;
import com.renergetic.common.repository.DemandScheduleRepository;
import com.renergetic.hdrapi.service.utils.OffSetPaging;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DemandRequestService {
    @Autowired
    DemandScheduleRepository demandScheduleRepository;
    @Autowired
    DemandDefinitionRepository demandDefinitionRepository;

    @Autowired
    UserService userService;

    public List<DemandScheduleDAO> save(List<DemandScheduleDAO> demandScheduleDAOS) throws InvalidCreationIdAlreadyDefinedException{
        LocalDateTime currentDateTime = LocalDateTime.now();

        Optional<DemandScheduleDAO> demandRequest = demandScheduleDAOS.stream().filter(x -> x.getId() != null).findAny();
        if (demandRequest.isPresent())
            throw new InvalidCreationIdAlreadyDefinedException();

        List<DemandSchedule> processed = demandScheduleDAOS.stream().map(d -> {
            d.setUpdate(DateConverter.toEpoch(currentDateTime));
            return d.mapToEntity();
        }).collect(Collectors.toList());

        return demandScheduleRepository.saveAll(processed).stream().map(DemandScheduleDAO::create).collect(Collectors.toList());
    }

    public DemandScheduleDAO save(DemandScheduleDAO demandScheduleDAO) throws InvalidCreationIdAlreadyDefinedException {
        demandScheduleDAO.setUpdate(DateConverter.toEpoch(LocalDateTime.now()));
        if (demandScheduleDAO.getId() != null && demandScheduleRepository.existsById(demandScheduleDAO.getId()))
            throw new InvalidCreationIdAlreadyDefinedException();

        return DemandScheduleDAO.create(demandScheduleRepository.save(demandScheduleDAO.mapToEntity()));
    }

    public DemandScheduleDAO update(DemandScheduleDAO demandRequestDAO) throws InvalidNonExistingIdException {
        demandRequestDAO.setUpdate(DateConverter.toEpoch(LocalDateTime.now()));
        if(!demandScheduleRepository.existsById(demandRequestDAO.getId()))
            throw new InvalidNonExistingIdException();

        return DemandScheduleDAO.create(demandScheduleRepository.save(demandRequestDAO.mapToEntity()));
    }

    public List<DemandScheduleDAO> getAll(long offset, int limit){
    	List<DemandScheduleDAO> list = demandScheduleRepository.findAll(new OffSetPaging(offset, limit)).stream().map(DemandScheduleDAO::create).collect(Collectors.toList());
    	
    	if (list != null && list.size() > 0)
    		return list;
    	else throw new NotFoundException("No demand schedules found");
    }

    public DemandScheduleDAO getById(Long id) {
        Optional<DemandSchedule> demandRequest = demandScheduleRepository.findById(id);
        if (demandRequest.isEmpty())
            throw new NotFoundException("No demand schedule with id " + id + " found");

        return DemandScheduleDAO.create(demandRequest.get());
    }

    public List<DemandScheduleDAO> getByUserId(Long userId, long offset, int limit) {
        List<DemandSchedule> demandRequest = demandScheduleRepository.findByUserId(userId, LocalDateTime.now(), offset, limit);

        List<DemandScheduleDAO> list = demandRequest.stream().map(DemandScheduleDAO::create).collect(Collectors.toList());
    	if (list != null && list.size() > 0)
    		return list;
    	else throw new NotFoundException("No demand schedules related with user " + userId + " found");
    }

    public List<DemandScheduleDAO> getByUserIdGroup(Long userId, long offset, int limit) throws InvalidNonExistingIdException {
        List<DemandSchedule> demandRequest = demandScheduleRepository.findByUserIdGroup(userId,0,1000);//, offset, limit);

        List<DemandScheduleDAO> list = demandRequest.stream().map(DemandScheduleDAO::create).collect(Collectors.toList());
    	return list;
//    	else throw new NotFoundException("No demand schedules related with user " + userId + " found");
    }

    public DemandScheduleDAO getByAssetIdAndActual(Long assetId){
        LocalDateTime localDateTime = LocalDateTime.now();
        Optional<DemandSchedule> demandRequest = demandScheduleRepository.findByAssetIdAndDemandStartLessThanEqualAndDemandStopGreaterThanEqual(assetId, localDateTime, localDateTime);
        if (demandRequest.isEmpty())
            throw new NotFoundException("No demand schedule related with asset  " + assetId + " found");

        return DemandScheduleDAO.create(demandRequest.get());
    }

    /* Basic CRUD for schedule definition */

    public DemandDefinitionDAO getDefinitionById(Long id){
        Optional<DemandDefinition> demandDefinition = demandDefinitionRepository.findById(id);
        if (demandDefinition.isEmpty())
            throw new NotFoundException("No demand definition with id  " + id + " found");
        
        return DemandDefinitionDAO.create(demandDefinition.get());
    }

    public DemandDefinitionDAO saveDefinition(DemandDefinitionDAO demandDefinitionDAO){
        if (demandDefinitionDAO.getId() !=  null && demandScheduleRepository.existsById(demandDefinitionDAO.getId()))
            throw new InvalidCreationIdAlreadyDefinedException();
        
        return DemandDefinitionDAO.create(demandDefinitionRepository.save(demandDefinitionDAO.mapToEntity()));
    }

    public DemandDefinitionDAO updateDefinition(DemandDefinitionDAO demandDefinitionDAO){
        Optional<DemandDefinition> demandDefinition = demandDefinitionRepository.findById(demandDefinitionDAO.getId());
        if (demandDefinition.isEmpty())
            throw new InvalidNonExistingIdException();
        return DemandDefinitionDAO.create(demandDefinitionRepository.save(demandDefinitionDAO.mapToEntity()));
    }

    public boolean deleteDefinition(Long id){
        Optional<DemandDefinition> demandDefinition = demandDefinitionRepository.findById(id);
        if (demandDefinition.isEmpty())
            throw new InvalidNonExistingIdException();
        demandDefinitionRepository.deleteById(id);
        return true;
    }

    public List<DemandDefinitionDAO> listDefinitions(long offset, int limit){
    	 List<DemandDefinitionDAO> list = demandDefinitionRepository.findAll(new OffSetPaging(offset, limit)).stream().map(DemandDefinitionDAO::create).collect(Collectors.toList());

     	if (list != null && list.size() > 0)
     		return list;
     	else throw new NotFoundException("No demand schedules exists");
    }
}