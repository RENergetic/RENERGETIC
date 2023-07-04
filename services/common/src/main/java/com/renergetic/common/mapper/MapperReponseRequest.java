package com.renergetic.common.mapper;

public interface MapperReponseRequest<E, Dres, Dreq> {
    public E toEntity(Dreq dto);
    public Dres toDTO(E entity);
}