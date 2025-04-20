package dn.tasktracker.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import dn.tasktracker.entity.TaskEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseForRedis {

    private Long id;
    private String username;
    private String password;
    private String createdAt;
    private String updatedAt;
    private List<TaskEntity> tasks;
    private String status;
    private String phoneNumber;
}
