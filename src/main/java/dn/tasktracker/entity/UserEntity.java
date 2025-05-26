package dn.tasktracker.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import java.io.Serializable;
import java.util.*;
@Entity
@Table(schema = "tasktracker", name = "users", indexes = @Index(name = "idx_users_username", columnList = "username"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BasedEntity implements Serializable{



    @Column(unique = true, nullable = false,length = 20)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user",cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 5)
    private List<TaskEntity> tasks = new ArrayList<>();

    @Column(nullable = false)
    private String status;

    private String photoUrl;

    @Column(nullable = false,unique = true)
    private String email;

    @OneToMany(mappedBy = "users",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Event> events = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @BatchSize(size = 10)
    @JsonManagedReference
    private Set<NotificationEntity> notifications = new HashSet<>();


    @Column(unique = true,length = 11)
    private String phoneNumber;


    public void addNotification(NotificationEntity notificationEntity){
        if (notificationEntity==null){
            notifications = new HashSet<>();
        }
        notifications.add(notificationEntity);
    }


    public void addTask(TaskEntity task){
        if (task==null){
            tasks = new ArrayList<>();
        }
        tasks.add(task);
    }

    public void addEvent(Event event){
        if (event == null){
            events = new HashSet<>();
        }
        events.add(event);
    }

    public void removeTask(Long id){
        tasks = tasks.stream()
                .filter(task -> task.getId().equals(id))
                .toList();
    }

    public void removeEvent(Event event){
        events.remove(event);
    }



}
