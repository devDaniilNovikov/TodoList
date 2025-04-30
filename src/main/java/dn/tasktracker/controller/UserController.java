package dn.tasktracker.controller;

import dn.tasktracker.dto.user.UserCreateRequest;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private static final String CREATE_ACCOUNT = "/api/v1/account/create";
    public static final String GET_ALL_USERS = "/api/v1/users";
    private static final String GET_ACCOUNT_BY_ID = "/api/v1/account/{id}";
    private static final String BAN_ACCOUNT = "/api/v1/account/ban";
    private static final String GET_USERS_BY_IDS = "/api/v1/search/accounts/";
    public static final String CHANGE_CREDENTIALS = "/api/v1/{id}/account/change/password";
    public static final String ADD_TASKS_FOR_USER = "/api/v1/{id}/account/add/tasks";
    private final UserService userService;

    @PostMapping(CREATE_ACCOUNT)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserResponse createAccount(@RequestBody UserCreateRequest userCreateRequest) {
        return userService.createAccount(userCreateRequest);
    }

    @GetMapping(GET_USERS_BY_IDS)
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getUsersByIds(@RequestParam List<Long> ids){
        return userService.findAllByIds(ids);
    }

    @GetMapping(GET_ALL_USERS)
    @ResponseStatus(HttpStatus.OK)
    public List<UserEntity> getUsers(@RequestParam(defaultValue = "0") int pageNumber,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        return userService.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @GetMapping(GET_ACCOUNT_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getAccountById(@PathVariable Long id){
        return userService.getById(id);
    }

    @PatchMapping(CHANGE_CREDENTIALS)
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@PathVariable Long id,
                               @RequestParam String oldPassword,
                               @RequestParam String newPassword) {
        userService.changePassword(oldPassword,newPassword,id);

    }

    @PostMapping(BAN_ACCOUNT)
    @ResponseStatus(HttpStatus.OK)
    public void banAccount(@RequestParam Long id){
        userService.banAccount(id);
    }

    @PostMapping(ADD_TASKS_FOR_USER)
    public UserResponse addTasksForUser(@PathVariable Long id,
                                @RequestParam List<Long> taskIds){
        return userService.setTasks(taskIds,id);
    }
}
