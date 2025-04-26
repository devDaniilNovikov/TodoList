package dn.tasktracker.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseForRedis {

    private Long id;
    private String title;
    private String description;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private String createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private String updatedAt;
    private List<TaskEntity> tasks;
    private Double rating;
}
