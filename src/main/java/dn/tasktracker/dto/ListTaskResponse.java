package dn.tasktracker.dto;

import dn.tasktracker.entity.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListTaskResponse {

    private List<TaskResponse> tasks = new ArrayList<>();

}
