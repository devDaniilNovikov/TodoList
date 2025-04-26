package dn.tasktracker.mapper;


import dn.tasktracker.dto.ListTaskResponse;
import dn.tasktracker.dto.TaskRequest;
import dn.tasktracker.dto.TaskResponse;
import dn.tasktracker.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {



//    TaskEntity toEntity(TaskRequest dto);

    TaskEntity toEntity(TaskResponse dto);

    TaskResponse toDto(TaskEntity taskEntity);

    List<TaskEntity> toEntityList(List<TaskResponse> dtoList);

    List<TaskResponse> toDtoList(List<TaskEntity> entityList);

    default ListTaskResponse mapToResponseList(List<TaskEntity> taskEntities){
        ListTaskResponse listTaskResponse = new ListTaskResponse();
        listTaskResponse.setTasks(taskEntities.stream()
                .map(this::toDto)
                .toList());
        return listTaskResponse;
    }

}
