package dn.tasktracker.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events", schema = "tasktracker", indexes = {
        @Index(name = "idx_events_task_id", columnList = "task_id"),
        @Index(name = "idx_events_user_id", columnList = "user_id")
})
public class Event extends BasedEntity {

    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",nullable = false)
    private TaskEntity tasks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private UserEntity users;

}
