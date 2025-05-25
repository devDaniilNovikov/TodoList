package dn.tasktracker.repository;

import dn.tasktracker.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity,Long>{
}
