package dn.tasktracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import software.amazon.awssdk.services.s3.model.JSONType;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BasedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(pattern = "yy/dd/HH",shape = JsonFormat.Shape.STRING)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yy/dd/HH",shape = JsonFormat.Shape.STRING)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
