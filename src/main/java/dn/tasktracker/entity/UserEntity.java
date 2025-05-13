package dn.tasktracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false,length = 20,name = "Имя пользователя")
    private String username;

    @Column(nullable = false,name = "Пароль")
    private String password;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow")
    @Column(name = "Дата создания")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow")
    @Column(name = "Дата обновления")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user",cascade = CascadeType.MERGE)
    @JsonBackReference
    @Column(name = "Задачи")
    private List<TaskEntity> tasks = new ArrayList<>();

    @Column(nullable = false,name = "Статус пользователя")
    private String status;

    private String photoUrl;

    @Column(nullable = false,unique = true,name = "Почта")
    private String email;

    @OneToMany(mappedBy = "users",cascade = CascadeType.ALL)
    @Column(name = "События")
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

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", tasks=" + tasks +
                ", status='" + status + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", events=" + events +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(tasks, that.tasks) && Objects.equals(status, that.status) && Objects.equals(photoUrl, that.photoUrl) && Objects.equals(events, that.events) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, createdAt, updatedAt, tasks, status, photoUrl, events, phoneNumber);
    }

    @Column(unique = true,length = 11)
    private String phoneNumber;



}
