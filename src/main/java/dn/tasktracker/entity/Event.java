package dn.tasktracker.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "event", timeToLive = 10000)
public class Event {
    @Id
    @Indexed
    private String id;
    private String name;
    private String description;
    private String to;
    private String from;
}
