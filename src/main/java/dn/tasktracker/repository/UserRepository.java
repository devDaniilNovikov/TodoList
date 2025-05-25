package dn.tasktracker.repository;

import dn.tasktracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> getByUsername(String username);

    Optional<UserEntity> getByPhoneNumber(String phone);

    @Query("select u from UserEntity u where u.id in :ids")
    List<UserEntity> findAllById(List<Long> ids);

    boolean existsByUsername(String username);

    @Query(value = "select u from UserEntity u join fetch u.tasks t where t.id in :ids")
    List<UserEntity> findAllByTasksIds(List<Long> ids);

    @Query("select u from UserEntity u join u.tasks t where t.title = :taskTitle")
    Optional<UserEntity> findByTaskTitle(String taskTitle);





}
