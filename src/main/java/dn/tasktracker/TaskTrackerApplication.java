package dn.tasktracker;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAsync
@EnableBatchProcessing
@EnableAspectJAutoProxy
public class TaskTrackerApplication {

    public static void main(String[] args) {
        File file = new File("logs");
        if(!file.exists() && !file.mkdir()){
            throw new RuntimeException("Can't create log directory");
        }
        SpringApplication.run(TaskTrackerApplication.class, args);
    }



}
