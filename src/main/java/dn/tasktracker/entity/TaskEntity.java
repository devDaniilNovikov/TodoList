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
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "task_id_seq")
    @SequenceGenerator(name = "task_id_seq", sequenceName = "task_id_seq",schema = "tasktracker",allocationSize = 1)
    private Long id;

    @Column(nullable = false)
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

    @ManyToMany(mappedBy = "tasks")
    @JsonBackReference
    private List<UserEntity> users = new ArrayList<>();

    private Long userId;

    private Long timeToComplete;

    private Double rating;

    public boolean isExpired(){
        return ChronoUnit.MINUTES
                .between(this.createdAt,
                        LocalDateTime.now()) >1;
    }

    public boolean isFailed() {
        return status.equals("FAILED");
    }



    public void addUser(List<UserEntity> users){
        this.users.addAll(users);
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
        return isCompletedAt() == that.isCompletedAt() && Objects.equals(getId(), that.getId()) && Objects.equals(getTitle(), that.getTitle()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getStatus(), that.getStatus()) && Objects.equals(getCreatedAt(), that.getCreatedAt()) && Objects.equals(getRequireUserId(), that.getRequireUserId()) && Objects.equals(getUpdatedAt(), that.getUpdatedAt()) && Objects.equals(getUsers(), that.getUsers()) && Objects.equals(getUserId(), that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getDescription(), getStatus(), isCompletedAt(), getCreatedAt(), getRequireUserId(), getUpdatedAt(), getUsers(), getUserId());
    }
}
