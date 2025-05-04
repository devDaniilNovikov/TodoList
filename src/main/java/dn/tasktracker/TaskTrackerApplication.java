package dn.tasktracker;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.EnableIntegrationManagement;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableAsync
@EnableBatchProcessing
@EnableAspectJAutoProxy
@EnableRabbit
@EnableTransactionManagement
@EnableRetry
public class TaskTrackerApplication {

    public static void main(String[] args) {
        File file = new File("logs");
        if(!file.exists() && !file.mkdir()){
            throw new RuntimeException("Can't create log directory");
        }
        SpringApplication.run(TaskTrackerApplication.class, args);
    }



}
