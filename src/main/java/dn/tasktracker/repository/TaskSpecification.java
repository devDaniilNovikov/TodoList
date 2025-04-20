package dn.tasktracker.repository;

import dn.tasktracker.dto.TaskSortDto;
import dn.tasktracker.entity.TaskEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public interface TaskSpecification {

    static Specification<TaskEntity> withFilter(TaskSortDto taskDto) {
        return Specification.where(byStatus(taskDto.getStatus())
                .and(byCreationDate(taskDto.getCreatedAt()))
                .and(byCreationDateUp())
                .and(byCreationDateDown())
                .and(byUpdatedAtDateUp()))
                .and(byCreationDateDown());
    }


    static Specification<TaskEntity> byStatus(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    static Specification<TaskEntity> byCreationDate(LocalDateTime creationTime){
        return (root, query, criteriaBuilder) -> {
            if (creationTime==null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"),creationTime);
        };
    }

    static Specification<TaskEntity> byCreationDateUp(){
        return (root, query, criteriaBuilder) -> {
            if (query==null){
                return null;
            }
            query.orderBy(criteriaBuilder.asc(root.get("createdAt")));
            return criteriaBuilder.conjunction();
        };
    }

    static Specification<TaskEntity> byCreationDateDown(){
        return (root, query, criteriaBuilder) -> {
            if (query==null){
                return null;
            }
            query.orderBy(criteriaBuilder.asc(root.get("createdAt")));
            return criteriaBuilder.conjunction();
        };
    }

    static Specification<TaskEntity> byUpdatedAtDateUp(){
        return (root, query, criteriaBuilder) -> {
            if (query==null){
                return null;
            }
            query.orderBy(criteriaBuilder.asc(root.get("updatedAt")));
            return criteriaBuilder.conjunction();
        };
    }

    static Specification<TaskEntity> byUpdatedAtDateDown(){
        return (root, query, criteriaBuilder) -> {
            if (query==null){
                return null;
            }
            query.orderBy(criteriaBuilder.asc(root.get("updatedAt")));
            return criteriaBuilder.conjunction();
        };
    }



}
