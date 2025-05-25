package dn.tasktracker.web.dto.notifications;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;

    private String content;

    private String from;

    private String to;

    @JsonFormat(pattern = "yy/dd/HH",shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt = LocalDateTime.now();
}
