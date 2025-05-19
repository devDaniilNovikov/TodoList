package dn.tasktracker.controller;

import dn.tasktracker.dto.user.*;
import dn.tasktracker.entity.UserEntity;
import dn.tasktracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Tag(name = "User",description = "Действия с аккаунтом пользователя")
public class UserController {

    private static final String CREATE_ACCOUNT = "/api/v1/account/create";
    private static final String GET_ALL_USERS = "/api/v1/accounts/all";
    private static final String GET_ACCOUNT_BY_ID = "/api/v1/account/{id}";
    private static final String BAN_ACCOUNT = "/api/v1/account/ban";
    private static final String GET_USERS_BY_IDS = "/api/v1/search/accounts/";
    private static final String CHANGE_CREDENTIALS = "/api/v1/{id}/account/change/password";
    private static final String CHANGE_EMAIL = "/api/v1/{id}/account/change/email";
    private static final String ACCOUNTS_LIST = "/api/v1/accounts/page";
    private static final String DELETE_ACCOUNT = "/api/v1/account/delete/{id}";
    private static final String DELETE_ALL_ACCOUNTS = "/api/v1/accounts/delete";
    private static final String GET_USERS_BY_TASK_IDS = "/api/v1/users-by-taskIds";
    private final UserService userService;

    @GetMapping(GET_USERS_BY_TASK_IDS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение списка пользователей по уникальному идентификатору задач, которые им присвоены")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "404",description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public ListUserResponse findAllByTaskIds(@RequestParam List<Long> taskIds){
        return userService.findAllByUsersTasksIds(taskIds);
    }

    @GetMapping(ACCOUNTS_LIST)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение списка пользователей постгранично v1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "404",description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public ListUserResponse listUserResponse(@RequestParam(defaultValue = "0") int pageNumber,
                                             @RequestParam(defaultValue = "10") int pageSize) {
        return userService.findAllUsersWithPagination(pageNumber,pageSize);
    }

    @PostMapping(CREATE_ACCOUNT)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Создание пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400",description = "Пользователь не создан, некорректные данные"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public UserResponse createAccount(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        return userService.createAccount(userCreateRequest);
    }

    @GetMapping(GET_USERS_BY_IDS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение списка пользователей по их уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "404",description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public ListUserResponse getUsersByIds(@RequestParam List<Long> userIds){
        return userService.findAllUsersByIds(userIds);
    }

    @GetMapping(GET_ALL_USERS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение списка пользователей постгранично v2")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи получены"),
            @ApiResponse(responseCode = "404",description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public ListUserResponse getUsers() {
        return userService.findAllUsers();
    }

    @GetMapping(GET_ACCOUNT_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Получение пользователя по его уникальному идентификатору ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь получен"),
            @ApiResponse(responseCode = "404",description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public UserResponse getAccountById(@PathVariable Long id){
        return userService.getById(id);
    }

    @PatchMapping(CHANGE_CREDENTIALS)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Изменение пароля пользователя по его уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль пользователя изменен"),
            @ApiResponse(responseCode = "400",description = "Некорректные данные"),
            @ApiResponse(responseCode = "404",description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public void changePassword(@PathVariable Long id,
                               @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(changePasswordDto,id);

    }

    @PostMapping(BAN_ACCOUNT)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Бан пользователя по его уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь забанен"),
            @ApiResponse(responseCode = "400",description = "Некорректные данные"),
            @ApiResponse(responseCode = "404",description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500",description = "Неизвестная ошибка сервера")
    })
    public void banAccount(@RequestParam Long id){
        userService.banAccount(id);
    }


    @PatchMapping(CHANGE_EMAIL)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(description = "Измнение почты пользователя по его уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Почта пользователя изменена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найдены"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка сервера")
    })
    public void changeEmail(@PathVariable Long id,
                            @RequestParam String email){
        userService.changeEmailForUser(email,id);
    }

    @DeleteMapping(DELETE_ACCOUNT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Удаление аккаунта пользователя по его уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Аккаунт пользователя удален"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка сервера")
    })
    public void deleteAccount(@PathVariable Long id){
        userService.deleteAccount(id);
    }

    @DeleteMapping(DELETE_ALL_ACCOUNTS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Удаление нескольких аккаунтов по их уникальному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Аккаунты удалены"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "500", description = "Неизвестная ошибка сервера")
    })
    public void deleteAllAccount(@RequestParam Set<Long> ids){
        userService.deleteAllByIds(ids);
    }
}
