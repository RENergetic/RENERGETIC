package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.dao.AssetCategoryDAO;
import com.renergetic.hdrapi.exception.InvalidNonExistingIdException;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.AssetCategory;
import com.renergetic.hdrapi.repository.AssetCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetCategoryService {
    @Autowired
    AssetCategoryRepository assetCategoryRepository;

    public AssetCategoryDAO save(AssetCategoryDAO assetCategoryDAO){
        return AssetCategoryDAO.create(assetCategoryRepository.save(assetCategoryDAO.mapToEntity()));
    }

    public AssetCategoryDAO update(AssetCategoryDAO assetCategoryDAO) {
        boolean assetExists = assetCategoryRepository.existsById(assetCategoryDAO.getId());

        if ( assetExists ) {
            return AssetCategoryDAO.create(assetCategoryRepository.save(assetCategoryDAO.mapToEntity()));
        } else throw new InvalidNonExistingIdException("The asset category to update doesn't exists");
    }

    public List<AssetCategoryDAO> list(){
        return assetCategoryRepository.findAll().stream().map(AssetCategoryDAO::create).collect(Collectors.toList());
    }

    public AssetCategoryDAO getById(Long id){
        AssetCategory assetCategory = assetCategoryRepository.findById(id).orElse(null);
        if (assetCategory != null)
            return AssetCategoryDAO.create(assetCategory);
        else
            throw new NotFoundException("No asset category found related with id " + id);
    }

    public boolean deleteById(Long id){
        if (id != null && assetCategoryRepository.existsById(id)) {
            assetCategoryRepository.deleteById(id);
            return true;
        }
        else
            return false;
    }

    public List<AssetCategoryDAO> search(String term){
        return assetCategoryRepository.findByNameContaining(term).stream().map(AssetCategoryDAO::create).collect(Collectors.toList());
    }
}
