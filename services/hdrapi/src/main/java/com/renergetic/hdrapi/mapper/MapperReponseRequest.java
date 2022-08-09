package com.renergetic.hdrapi.mapper;

public interface MapperReponseRequest<E, Dres, Dreq> {
    public E toEntity(Dreq dto);
    public Dres toDTO(E entity);
}