package dn.tasktracker.repository;

import dn.tasktracker.entity.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<Event,String> {


}
