package dn.tasktracker.web.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import dn.tasktracker.web.dto.TaskResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "UserResponse",description = "Список пользователей с применением пагинации")
public class UserResponse implements Serializable {

    @Schema(name = "id",description = "Уникальный идентификатор пользователя")
    private Long id;

    @Schema(name = "username",description = "Имя пользователя")
    private String username;

    @Schema(name = "password",description = "Пароль пользователя")
    private String password;

    @JsonFormat(pattern = "dd.MM.yyyy || HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow")
    @Schema(name = "createdAt",description = "Дата и время создания пользователя")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd.MM.yyyy || HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow")
    @Schema(name = "updatedAt",description = "Дата и время обновления пользователя")
    private LocalDateTime updatedAt;

    @Schema(name = "tasks",description = "Задачи пользователя")
    @Nullable
    private List<TaskResponse> tasks;

    @Schema(name = "email",description = "Электронная почта пользователя")
    @Email
    private String email;

    @Schema(name = "status",description = "Статус пользователя")
    private String status;

    @Schema(name = "phoneNumber",description = "Номер телефона пользователя")
    private String phoneNumber;


}
