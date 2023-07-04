package com.renergetic.common.mapper;

public interface Mapper<E, D> {
    public E toEntity(D dto);
    public D toDTO(E entity);
}