package dn.tasktracker.mapper;

import dn.tasktracker.dto.user.ListUserResponse;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserEntity toEntity(UserResponse userResponse);

    UserResponse toDto(UserEntity userEntity);

    List<UserResponse> toDtoList(List<UserEntity> userEntityList);

    List<UserEntity> toEntityList(List<UserResponse> userResponseList);

//    default ListUserResponse mapToResponseList(List<UserEntity> userEntities) {
//        ListUserResponse listUserResponse = new ListUserResponse();
//        Page<UserResponse> page = new PageImpl<>(userEntities.stream().map(this::toDto).toList());
//        listUserResponse.setUsers(page);
//        return listUserResponse;
//    }



}
