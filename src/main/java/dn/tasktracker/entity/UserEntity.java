package dn.tasktracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false,length = 20)
    private String username;

    @Column(nullable = false)
    private char[] password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(inverseJoinColumns = @JoinColumn(name = "task_id"),
    joinColumns = @JoinColumn(name = "user_id"),schema = "tasktracker")
    private Set<TaskEntity> tasks = new HashSet<>();


}
