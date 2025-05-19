package dn.tasktracker.mapper;

import dn.tasktracker.dto.TaskResponse;
import dn.tasktracker.dto.user.ListUserResponse;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {


    UserEntity toEntity(UserResponse userResponse);

    UserResponse toDto(UserEntity userEntity);

    List<UserResponse> toDtoList(List<UserEntity> userEntityList);

    List<UserEntity> toEntityList(List<UserResponse> userResponseList);

    default ListUserResponse toList(List<UserEntity>users){
        ListUserResponse listUserResponse = new ListUserResponse();
        listUserResponse.setUsers(users.stream()
                .map(this::toDto)
                .toList());
        return listUserResponse;
    }



}
