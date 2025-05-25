package dn.tasktracker.web.dto.notifications;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Notification",description = "ДТО с запросом уведомления")
public class NotificationRequest {

    @Schema(name = "id",description = "Уникальный идентификатор уведомления")
    private Long id;

    @Schema(name = "content",description = "Контент уведомления")
    private String content;

    @Schema(name = "from",description = "Имя пользователя, от которого пришло уведомление")
    @Nullable
    private String from;


    @Schema(name = "to",description = "Имя пользователя, которому направлено уведомление")
    private String to;

    @Schema(name = "createdAt",description = "Время создания уведомления")
    @JsonFormat(pattern = "yy.dd.HH",shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;
}
