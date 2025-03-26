package dn.tasktracker.mapper;


import dn.tasktracker.dto.TaskDto;
import dn.tasktracker.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {


    TaskEntity toEntity(TaskDto dto);

    TaskDto toDto(TaskEntity  taskEntity);

    List<TaskEntity> toEntityList(List<TaskDto> dtoList);

    List<TaskDto> toDtoList(List<TaskEntity> entityList);

}
