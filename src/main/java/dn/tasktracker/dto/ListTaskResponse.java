package dn.tasktracker.dto;

import dn.tasktracker.dto.user.UserResponse;
import dn.tasktracker.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ListUserResponse",description = "ДТО со списком задач с применением пагинации")
public class ListTaskResponse {

    @Schema(name = "tasks",description = "Список задач с применением пагинации")
    private List<TaskResponse> tasks = new ArrayList<>();

}
