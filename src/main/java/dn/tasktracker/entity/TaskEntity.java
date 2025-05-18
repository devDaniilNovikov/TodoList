package dn.tasktracker.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Entity
@Table(schema = "tasktracker", name = "tasks",
        indexes = {
                @Index(name = "idx_tasks_user_id", columnList = "user_id"),
                @Index(name = "idx_tasks_title", columnList = "title")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd||HH:mm")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "user_id",nullable = false)
    @JsonBackReference
    private UserEntity user;

    @OneToMany(mappedBy = "tasks",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SubTaskEntity> subTasks = new ArrayList<>();

    @OneToMany(mappedBy = "tasks",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Event> events = new HashSet<>();

    public boolean isExpired(){
        return ChronoUnit.MINUTES
                .between(this.createdAt,
                        LocalDateTime.now()) >1;
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
        if (o == null || getClass() != o.getClass()) return false;
        TaskEntity that = (TaskEntity) o;
        return completedAt == that.completedAt && Objects.equals(id, that.id)
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(status, that.status)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt)
                && Objects.equals(user, that.user)
                && Objects.equals(subTasks, that.subTasks)
                && Objects.equals(events, that.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, completedAt, createdAt, updatedAt, user, subTasks, events);
    }
}
