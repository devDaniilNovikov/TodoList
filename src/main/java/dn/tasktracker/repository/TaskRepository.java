package dn.tasktracker.repository;

import dn.tasktracker.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.stream.Task;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, String>, JpaSpecificationExecutor<TaskEntity> {

    Optional<TaskEntity> findByTitle(String title);

    @Query("select t from TaskEntity t where t.status = :status")
    Optional<TaskEntity> findByStatus(@Param("status") String status);


}
