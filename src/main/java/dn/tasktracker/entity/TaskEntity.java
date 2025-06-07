package dn.tasktracker.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
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
public class TaskEntity extends BasedEntity {

    @Column(nullable = false,unique = true)
    private String title;

    private String description;

    @Column(nullable = false)
    private String status;

    private boolean completedAt;


    @ManyToOne(fetch = FetchType.LAZY,cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "user_id",nullable = false)
    @JsonBackReference
    private UserEntity user;

    @OneToMany(mappedBy = "tasks",fetch = FetchType.LAZY)
    @JsonIgnore
    @BatchSize(size = 10)
    private List<SubTaskEntity> subTasks = new ArrayList<>();

    @OneToMany(mappedBy = "tasks",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Event> events = new HashSet<>();

    public boolean isExpired(){
        return ChronoUnit.MINUTES
                .between(getCreatedAt(),
                        LocalDateTime.now()) >1;
    }


}
