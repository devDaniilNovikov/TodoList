package dn.tasktracker.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Configuration;


@RequiredArgsConstructor
@EnableRabbit
@Configuration

public class RabbitConfig {

    private final RabbitProperties rabbitProperties;

}
