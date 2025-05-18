package dn.tasktracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;
import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ListUserResponse",description = "Выходящее ДТО задачи")
public class TaskResponse {

    @Schema(name = "id",description = "Уникальный идентификатор задачи")
    private Long id;

    @Schema(name = "title",description = "Название задачи")
    private String title;

    @Schema(name = "description",description = "Описание задачи")
    private String description;

    @Schema(name = "status",description = "Статус задачи")
    private String status;

    @Schema(name = "createdAt",description = "Дата и время создания задачи")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt",description = "Дата и время обновления задачи")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private LocalDateTime updatedAt;

    @Schema(name = "workerName",description = "Имя пользователя , для которого создана задача")
    private String workerName;


}
