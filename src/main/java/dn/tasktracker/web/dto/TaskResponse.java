package dn.tasktracker.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ListUserResponse",description = "Выходящее ДТО задачи")
public class TaskResponse implements Serializable {

    @Schema(name = "id",description = "Уникальный идентификатор задачи")
    private Long id;

    @Schema(name = "title",description = "Название задачи")
    private String title;

    @Schema(name = "description",description = "Описание задачи")
    private String description;

    @Schema(name = "status",description = "Статус задачи")
    private String status;

    @Schema(name = "createdAt",description = "Дата и время создания задачи")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "dd.MM.yyyy || HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt",description = "Дата и время обновления задачи")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "dd.MM.yyyy || HH:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(name = "workerName",description = "Имя пользователя, для которого создана задача")
    private String workerName;


}
