package dn.tasktracker.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import dn.tasktracker.entity.UserEntity;
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
public class TaskRequest implements Serializable {
    @NotNull(message = "title cannot be blank")
    private String title;
    @NotNull(message = "description cannot be blank")
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private LocalDateTime updatedAt;
    @NotNull
    private Long userId;
    @NotNull
    private Double rating;


}
