package dn.tasktracker.service;

@FunctionalInterface
public interface EmailService {

    void sendEmail(String to, String subject, String body);
}
