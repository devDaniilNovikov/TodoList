package dn.tasktracker.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailSendingConfig {

    private static final String MAIL_PROTOCOL = "mail.transport.protocol";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String MAIL_DEBUG = "mail.debug";


    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.properties.mail.starttls.enable}")
    private String isStarttlsEnabled;

    @Value("${spring.mail.protocol}")
    private String protocol;
    
    @Value("${spring.properties.mail.auth}")
    private String isAuthEnabled;

    @Value("${spring.mail.default-encoding}")
    private String defaultEncoding;

    @Value("${spring.properties.mail.debug}")
    private String isDebugEnabled;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setDefaultEncoding(defaultEncoding);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put(MAIL_PROTOCOL, protocol);
        properties.put(MAIL_SMTP_AUTH, isAuthEnabled);
        properties.put(MAIL_SMTP_STARTTLS_ENABLE, isStarttlsEnabled);
        properties.put(MAIL_DEBUG,isDebugEnabled);
        return javaMailSender;

    }
}
