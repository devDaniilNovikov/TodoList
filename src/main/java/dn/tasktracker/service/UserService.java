package dn.tasktracker.service;

import dn.tasktracker.web.dto.user.ChangePasswordDto;
import dn.tasktracker.web.dto.user.ListUserResponse;
import dn.tasktracker.web.dto.user.UserCreateRequest;
import dn.tasktracker.web.dto.user.UserResponse;

import java.util.List;
import java.util.Set;

public interface UserService {

    ListUserResponse findAllUsersByIds(List<Long> userIds);

    ListUserResponse findAllByUsersTasksIds(List<Long> taskIds);

    ListUserResponse findAllUsers();

    ListUserResponse findAllUsersWithPagination(int pageNumber,int pageSize);

    UserResponse createAccount(UserCreateRequest userCreateRequest);

    UserResponse getById(Long userId);

    UserResponse getByTaskTitle(String taskTitle);

    UserResponse getByUsername(String username);

    UserResponse getByPhoneNumber(String phoneNumber);

    void banAccount(Long userId);

    void deleteAccount(Long userId);

    void changePassword(ChangePasswordDto changePasswordDto, Long userId);

    void changeEmailForUser(String email, Long userId);

    void deleteAllUsersByIds(Set<Long> ids);











}
