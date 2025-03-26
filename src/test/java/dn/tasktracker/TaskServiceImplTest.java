package dn.tasktracker;

import dn.tasktracker.dto.TaskDto;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.exception.TaskNotFoundException;
import dn.tasktracker.mapper.TaskMapper;
import dn.tasktracker.repository.TaskRepository;
import dn.tasktracker.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;


    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    public void testSave() {
        TaskDto taskDto = new TaskDto();
        taskDto.setTitle("Test Task");
        taskDto.setStatus("In Progress");
        taskDto.setDescription("This is a test task");

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId("1");
        taskEntity.setTitle("Test Task");
        taskEntity.setStatus("In Progress");
        taskEntity.setDescription("This is a test task");
        taskEntity.setCreatedAt(LocalDateTime.now());
        taskEntity.setUpdatedAt(LocalDateTime.now());
        taskEntity.setCompletedAt(false);

        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
        when(taskMapper.toDto(taskEntity)).thenReturn(taskDto);

        TaskDto savedTaskDto = taskService.save(taskDto);

        assertEquals(taskDto, savedTaskDto);
        verify(taskRepository, times(1)).save(any(TaskEntity.class));
        verify(taskMapper, times(1)).toDto(taskEntity);
    }

    @Test
    public void testGetByIdNotFound() {
        String taskId = "20ba8263-9c3b-4234-bb8b-d5a2289ce987";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getById(taskId));
        verify(taskRepository, times(1)).findById(taskId);
    }


        @Test
        public void testFindByTitle() {
            String title = "Test Task";

            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId("1");
            taskEntity.setTitle(title);
            taskEntity.setStatus("In Progress");
            taskEntity.setDescription("This is a test task");
            taskEntity.setCreatedAt(LocalDateTime.now());
            taskEntity.setUpdatedAt(LocalDateTime.now());
            taskEntity.setCompletedAt(false);

            TaskDto taskDto = new TaskDto();
            taskDto.setId("1");
            taskDto.setTitle(title);
            taskDto.setStatus("In Progress");
            taskDto.setDescription("This is a test task");

            when(taskRepository.findByTitle(title)).thenReturn(Optional.of(taskEntity));
            when(taskMapper.toDto(taskEntity)).thenReturn(taskDto);

            TaskDto retrievedTaskDto = taskService.findByTitle(title);

            assertEquals(taskDto, retrievedTaskDto);
            verify(taskRepository, times(1)).findByTitle(title);
            verify(taskMapper, times(1)).toDto(taskEntity);
        }

        @Test
        public void testFindByTitleNotFound() {
            String title = "Test Task";

            when(taskRepository.findByTitle(title)).thenReturn(Optional.empty());

            assertThrows(TaskNotFoundException.class, () -> taskService.findByTitle(title));
            verify(taskRepository, times(1)).findByTitle(title);
        }

        @Test
        public void testChangeStatus() {
            String taskId = "1";
            String status = "Completed";

            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId(taskId);
            taskEntity.setTitle("Test Task");
            taskEntity.setStatus("In Progress");
            taskEntity.setDescription("This is a test task");
            taskEntity.setCreatedAt(LocalDateTime.now());
            taskEntity.setUpdatedAt(LocalDateTime.now());
            taskEntity.setCompletedAt(false);

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

            taskService.changeStatus(taskId, status);

            verify(taskRepository, times(1)).findById(taskId);
            verify(taskRepository, times(1)).save(taskEntity);
        }

        @Test
        public void testChangeStatusNotFound() {
            String taskId = "1";
            String status = "Completed";

            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            taskService.changeStatus(taskId, status);

            verify(taskRepository, times(1)).findById(taskId);
        }

        @Test
        public void testUpdate() {
            String taskId = "1";

            TaskDto taskDto = new TaskDto();
            taskDto.setStatus("Completed");
            taskDto.setDescription("This is an updated test task");

            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId(taskId);
            taskEntity.setTitle("Test Task");
            taskEntity.setStatus("In Progress");
            taskEntity.setDescription("This is a test task");
            taskEntity.setCreatedAt(LocalDateTime.now());
            taskEntity.setUpdatedAt(LocalDateTime.now());
            taskEntity.setCompletedAt(false);

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

            taskService.update(taskId, taskDto);

            verify(taskRepository, times(1)).findById(taskId);
            verify(taskRepository, times(1)).save(taskEntity);
        }

        @Test
        public void testUpdateNotFound() {
            String taskId = "1";

            TaskDto taskDto = new TaskDto();
            taskDto.setStatus("Completed");
            taskDto.setDescription("This is an updated test task");

            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            assertThrows(TaskNotFoundException.class, () -> taskService.update(taskId, taskDto));
            verify(taskRepository, times(1)).findById(taskId);
        }

        @Test
        public void testDelete() {
            String taskId = "1";

            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId(taskId);
            taskEntity.setTitle("Test Task");
            taskEntity.setStatus("In Progress");
            taskEntity.setDescription("This is a test task");
            taskEntity.setCreatedAt(LocalDateTime.now());
            taskEntity.setUpdatedAt(LocalDateTime.now());
            taskEntity.setCompletedAt(false);

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));

            taskService.delete(taskId);

            verify(taskRepository, times(1)).findById(taskId);
            verify(taskRepository, times(1)).delete(taskEntity);
        }

        @Test
        public void testDeleteNotFound() {
            String taskId = "1";

            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            assertThrows(TaskNotFoundException.class, () -> taskService.delete(taskId));
            verify(taskRepository, times(1)).findById(taskId);
        }

        @Test
        public void testDeleteAll() {
            List<String> ids = List.of("1", "2", "3");

            TaskEntity taskEntity1 = new TaskEntity();
            taskEntity1.setId("1");
            taskEntity1.setTitle("Test Task 1");
            taskEntity1.setStatus("In Progress");
            taskEntity1.setDescription("This is a test task 1");
            taskEntity1.setCreatedAt(LocalDateTime.now());
            taskEntity1.setUpdatedAt(LocalDateTime.now());
            taskEntity1.setCompletedAt(false);

            TaskEntity taskEntity2 = new TaskEntity();
            taskEntity2.setId("2");
            taskEntity2.setTitle("Test Task 2");
            taskEntity2.setStatus("In Progress");
            taskEntity2.setDescription("This is a test task 2");
            taskEntity2.setCreatedAt(LocalDateTime.now());
            taskEntity2.setUpdatedAt(LocalDateTime.now());
            taskEntity2.setCompletedAt(false);

            TaskEntity taskEntity3 = new TaskEntity();
            taskEntity3.setId("3");
            taskEntity3.setTitle("Test Task 3");
            taskEntity3.setStatus("In Progress");
            taskEntity3.setDescription("This is a test task 3");
            taskEntity3.setCreatedAt(LocalDateTime.now());
            taskEntity3.setUpdatedAt(LocalDateTime.now());
            taskEntity3.setCompletedAt(false);

            List<TaskEntity> taskEntities = List.of(taskEntity1, taskEntity2, taskEntity3);

            when(taskRepository.findAllById(ids)).thenReturn(taskEntities);

            taskService.deleteAll(ids);

            verify(taskRepository, times(1)).findAllById(ids);
            verify(taskRepository, times(3)).deleteAllById(ids);
        }
    }

