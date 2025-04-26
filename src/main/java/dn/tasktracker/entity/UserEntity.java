package dn.tasktracker.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(schema = "tasktracker", name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(schema = "tasktracker",name = "user_id_seq",sequenceName = "user_id_seq",allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false,length = 20)
    private String username;

    @Column(nullable = false)
    private String password;

    private Double rating;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING, timezone = "Europe/Moscow")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private List<TaskEntity> tasks = new ArrayList<>();

    public void addTask(TaskEntity task){
        tasks.add(task);
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", rating=" + rating +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", tasks=" + tasks +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity user = (UserEntity) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(rating, user.rating) && Objects.equals(createdAt, user.createdAt) && Objects.equals(updatedAt, user.updatedAt) && Objects.equals(tasks, user.tasks) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(status, user.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, rating, createdAt, updatedAt, tasks, phoneNumber, status);
    }

    @Column(unique = true,length = 11)
    private String phoneNumber;


    private String status;


}
