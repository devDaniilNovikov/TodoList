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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(schema = "tasktracker", name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(schema = "tasktracker",name = "user_id_seq",sequenceName = "user_id_seq",allocationSize = 1)
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "task_id"),
    joinColumns = @JoinColumn(name = "user_id"),schema = "tasktracker")
    @JsonBackReference
    private List<TaskEntity> tasks = new ArrayList<>();

    public void addTask(TaskEntity task){
        tasks.add(task);
    }



    @Column(unique = true,length = 11)
    private String phoneNumber;


    private String status;


}
