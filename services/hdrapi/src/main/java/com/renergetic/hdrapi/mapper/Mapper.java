package com.renergetic.hdrapi.mapper;

public interface Mapper<E, D> {
    public E toEntity(D dto);
    public D toDTO(E entity);
}