package dn.tasktracker.service;

import dn.tasktracker.dto.user.*;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface UserService {

    ListUserResponse findAllUsersByIds(List<Long> userIds);

    ListUserResponse findAllByUsersTasksIds(List<Long> taskIds);

    ListUserResponse findAllUsers();

    ListUserResponse findAllUsersWithPagination(int pageNumber,int pageSize);

    UserResponse createAccount(UserCreateRequest userCreateRequest);

    UserResponse getById(Long userId);

    UserResponse getByUsername(String username);

    UserResponse getByPhoneNumber(String phoneNumber);

    void banAccount(Long userId);

    void deleteAccount(Long userId);

    void changePassword(ChangePasswordDto changePasswordDto,Long userId);

    void changeEmailForUser(String email, Long userId);

    void deleteAllByIds(Set<Long> ids);











}
