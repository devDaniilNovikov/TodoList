package dn.tasktracker.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import dn.tasktracker.dto.TaskResponse;
import dn.tasktracker.entity.TaskEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jdk.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "ListUserResponse",description = "Список пользователей с применением пагинации")
public class UserResponse {

    @Schema(name = "id",description = "Уникальный идентификатор пользователя")
    private Long id;

    @Schema(name = "username",description = "Имя пользователя")
    private String username;

    @Schema(name = "password",description = "Пароль пользователя")
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow")
    @Schema(name = "createdAt",description = "Дата и время создания пользователя")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow")
    @Schema(name = "updatedAt",description = "Дата и время обновления пользователя")
    private LocalDateTime updatedAt;

    @Schema(name = "tasks",description = "Задачи пользователя")
    @Nullable
    private List<TaskResponse> tasks;

    @Schema(name = "status",description = "Статус пользователя")
    private String status;

    @Schema(name = "phoneNumber",description = "Номер телефона пользователя")
    private String phoneNumber;


}
