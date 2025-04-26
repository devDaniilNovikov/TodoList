package dn.tasktracker.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Entity
@Table(schema = "tasktracker",name = "tasks",indexes = {
        @Index(columnList = "id",name = "task_id_idx",unique = true)}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(name = "task_id_seq", sequenceName = "task_id_seq",schema = "tasktracker",allocationSize = 1)
    private Long id;

    @Column(nullable = false,unique = true)
    private String title;

    private String description;

    @Column(nullable = false)
    private String status;

    private boolean completedAt;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private LocalDateTime createdAt;

    private Long requireUserId;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST,
    CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name = "user_id",nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "tasks")
    private List<SubTaskEntity> subTasks = new ArrayList<>();


    public boolean isExpired(){
        return ChronoUnit.MINUTES
                .between(this.createdAt,
                        LocalDateTime.now()) >1;
    }

    public boolean isFailed() {
        return status.equals("FAILED");
    }





    @Override
    public String toString() {
        return "TaskEntity{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", completedAt=" + completedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TaskEntity that)) return false;
        return completedAt == that.completedAt && Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(createdAt, that.createdAt) && Objects.equals(requireUserId, that.requireUserId) && Objects.equals(updatedAt, that.updatedAt) && Objects.equals(user, that.user) && Objects.equals(subTasks, that.subTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, completedAt, createdAt, requireUserId, updatedAt, user, subTasks);
    }
}
