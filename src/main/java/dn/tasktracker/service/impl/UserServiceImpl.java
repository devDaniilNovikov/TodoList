package dn.tasktracker.service.impl;

import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.repository.UserRepository;
import dn.tasktracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());






    @Override
    public void createAccount(String username, char[] password) {

    }

    @Override
    public void banAccount(String userId) {

    }

    @Override
    public void deleteAccount(String userId) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword, String userId) {

    }

    @Override
    public void setTasks(Set<TaskEntity> tasks, String userId) {

    }
}
