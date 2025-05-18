package dn.tasktracker.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import dn.tasktracker.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ListUserResponse",description = "Запрос на создание задачи")
public class TaskRequest implements Serializable {

    @NotBlank(message = "Описание не может быть пустым!")
    @Schema(name = "description",description = "Описание задачи")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    @Schema(name = "createdAt",description = "Дата и время создания задачи")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt",description = "Дата и время обновления")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private LocalDateTime updatedAt;

    @Schema(name = "userId",description = "Уникальный идентификатор пользователя , для которого создается задача")
    @NotBlank(message = "Уникальный идентификатор пользователя не может быть пустым!")
    private Long userId;


}
