package dn.tasktracker.configuration.web;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentManager;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class WebConfig{

    @Bean
    public RestClient restClient(){
        return RestClient.builder().build();
    }






}
