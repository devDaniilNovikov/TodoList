package dn.tasktracker.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ListUserResponse",description = "ДТО со списком задач")
public class ListTaskResponse implements Serializable {

    @Schema(name = "tasks",description = "Список задач")
    private List<TaskResponse> tasks = new ArrayList<>();

}
