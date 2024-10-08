package com.renergetic.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.renergetic.api.dao.ExampleRequest;
import com.renergetic.api.dao.ExampleResponse;
import com.renergetic.api.exception.NotFoundException;
import com.renergetic.api.model.ExampleEntity;
import com.renergetic.api.repository.ExampleRepository;


@Service
public class ExampleService {

    @Autowired
    private ExampleRepository exampleRepository;

    public List<ExampleResponse> get(Integer offset, Integer limit) {
        return exampleRepository
        .findAll(Pageable.ofSize(limit).withPage(offset))
        .stream()
        .map(ExampleResponse::create).collect(Collectors.toList());
    }

    public ExampleResponse create(ExampleRequest example) {
        return ExampleResponse.create(exampleRepository.save(example.mapToEntity()));
    }

    public ExampleResponse update(Long id, ExampleRequest example) {
        if (exampleRepository.existsById(id)) {
            ExampleEntity entity = example.mapToEntity();
            entity.setId(id);
            return ExampleResponse.create(exampleRepository.save(entity));
        } else throw new NotFoundException("Example with id " + id + " not found");
    }

    public void delete(Long id) {
        if (exampleRepository.existsById(id)) {
            exampleRepository.deleteById(id);
        }
    }
}
