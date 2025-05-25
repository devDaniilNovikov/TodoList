package dn.tasktracker.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Table(schema = "tasktracker",name = "notifications")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity extends BasedEntity implements Serializable {


    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserEntity user;

    private String content;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss",shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Override
    public String toString() {
        return "NotificationEntity{" +
                "user=" + user +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
