package dn.tasktracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(schema = "tasktracker", name = "users",
        indexes = @Index(name = "idx_users_username",
                columnList = "username"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false,length = 20)
    private String username;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow")
    private LocalDateTime updatedAt;

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

    @Column(unique = true,length = 11)
    private String phoneNumber;



}
