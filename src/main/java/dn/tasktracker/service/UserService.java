package dn.tasktracker.service;

import dn.tasktracker.dto.user.UserCreateRequest;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<UserResponse> findAllByIds(List<Long> ids);


    List<UserResponse> findAll(Pageable pageable);

    UserResponse createAccount(UserCreateRequest userCreateRequest);

    UserResponse getById(Long userId);

    UserResponse getByUsername(String username);

    UserResponse getByPhoneNumber(String phoneNumber);

    void banAccount(Long userId);

    void deleteAccount(Long userId);

    void changePassword(String oldPassword, String newPassword, Long userId);

    void setTasks(Set<TaskEntity> tasks, Long userId);











}
