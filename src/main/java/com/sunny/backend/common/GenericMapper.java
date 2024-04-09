package com.sunny.backend.common;

import java.util.List;

public interface GenericMapper<D, E> {
	//사용해서 리팩토링 해볼까?
	D toDto(E e); //엔티티를 DTO로 변환

	E toEntity(D d); //Dto

	List<D> toDtoList(List<E> entityList); //엔티티 리스트를 DTO 리스트로 변환

	List<E> toEntityList(List<D> dtoList); // DTO 리스트를 엔티티 리스트로 변환

}
