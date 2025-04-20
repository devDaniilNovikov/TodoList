package dn.tasktracker.repository;

import dn.tasktracker.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


public interface TaskRepository extends JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity> {

    Optional<TaskEntity> findByTitle(String title);


    @Query("SELECT t FROM TaskEntity t WHERE t.userId = :userId AND t.title LIKE %:title%")
    Optional<List<TaskEntity>> findAllByUserId(@Param("userId") Long userId);

    void deleteAllByUserId(Long userId);

}