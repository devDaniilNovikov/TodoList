package dn.tasktracker.controller;

import dn.tasktracker.dto.user.UserCreateRequest;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private static final String CREATE_ACCOUNT = "/api/v1/account/create";
    public static final String GET_ACCOUNT_BY_ID = "/api/v1/account/{id}";
    private static final String BAN_ACCOUNT = "/api/v1/account/ban/";
    private static final String GET_USERS_BY_IDS = "/api/v1/search/accounts/";



    private final UserService userService;

    @PostMapping(CREATE_ACCOUNT)
    public UserResponse createAccount(@RequestBody UserCreateRequest userCreateRequest) {
        return userService.createAccount(userCreateRequest);
    }

    @GetMapping(GET_USERS_BY_IDS)
    public List<UserResponse> getUsersByIds(@RequestParam List<Long> ids){
        return userService.findAllByIds(ids);
    }





    @GetMapping(GET_ACCOUNT_BY_ID)
    public UserResponse getAccountById(@PathVariable Long id){
        return userService.getById(id);
    }

    @PostMapping(BAN_ACCOUNT)
    public void banAccount(@RequestParam Long id){
        userService.banAccount(id);
    }
}
