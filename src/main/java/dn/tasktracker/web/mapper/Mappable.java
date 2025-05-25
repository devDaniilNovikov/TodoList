package dn.tasktracker.web.mapper;

import java.util.List;

public interface Mappable <E,D>{
    E toEntity(D dto);

    D toDto(E entity);

    default List<D> toDtoList(List<E> entityList) {
        return entityList.stream()
                .map(this::toDto)
                .toList();
    }
    default List<E> toEntityList(List<D> dtoList) {
        return dtoList.stream()
                .map(this::toEntity)
                .toList();
    }

}
