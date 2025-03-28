package dn.tasktracker.service;

import dn.tasktracker.entity.TaskEntity;

import java.util.Set;

public interface UserService {

    void createAccount(String username, char[] password);

    void banAccount(String userId);

    void deleteAccount(String userId);

    void changePassword(String oldPassword,
                        String newPassword,
                        String userId);

    void setTasks(Set<TaskEntity> tasks,
                  String userId);



}
