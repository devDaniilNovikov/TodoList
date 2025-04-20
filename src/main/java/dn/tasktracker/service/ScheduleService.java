package dn.tasktracker.service;

import org.hibernate.annotations.Comments;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public abstract class ScheduleService {

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteOldComments() {

    }

}
