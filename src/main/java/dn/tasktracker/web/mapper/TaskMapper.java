package dn.tasktracker.web.mapper;


import dn.tasktracker.web.dto.ListTaskResponse;
import dn.tasktracker.web.dto.TaskResponse;
import dn.tasktracker.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper extends Mappable<TaskEntity, TaskResponse> {


    @Mapping(source = "user.username", target = "workerName")
    @Override
    TaskResponse toDto(TaskEntity taskEntity);

    default ListTaskResponse mapToDtoList(List<TaskEntity> tasks){
        ListTaskResponse listTaskResponse = new ListTaskResponse();
        listTaskResponse.setTasks(tasks.stream()
                .map(this::toDto)
                .toList());
        return listTaskResponse;
    }


}
