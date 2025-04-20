package dn.tasktracker.mapper;

import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserEntity toEntity(UserResponse userResponse);

    UserResponse toDto(UserEntity userEntity);

    List<UserResponse> toDtoList(List<UserEntity> userEntityList);

    List<UserEntity> toEntityList(List<UserResponse> userResponseList);



}
