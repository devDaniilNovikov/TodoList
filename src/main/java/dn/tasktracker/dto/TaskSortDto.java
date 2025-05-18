package dn.tasktracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TaskSort",description = "ДТО с фильтрацией задачи по критериям")
public class TaskSortDto {

    @Schema(name = "createdAt",description = "Дата и время создания задачи")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private LocalDateTime createdAt;

    @Schema(name = "updatedAt",description = "Дата и время обновления задачи")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private LocalDateTime updatedAt;

    @Schema(name = "status",description = "Статус задачи")
    private String status;

    @Schema(name = "pageNumber",description = "Номер страницы")
    private Integer pageNumber;

    @Schema(name = "pageSize",description = "Количество элементов на странице")
    private Integer pageSize;
}
