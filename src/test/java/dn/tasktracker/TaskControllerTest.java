package dn.tasktracker;
import dn.tasktracker.controller.TaskController;
import dn.tasktracker.dto.TaskDto;
import dn.tasktracker.entity.TaskEntity;
import dn.tasktracker.service.TaskService;
import dn.tasktracker.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskServiceImpl taskService;

    @Test
    public void testFindAll() throws Exception {
        List<TaskEntity> taskEntities = List.of(
                new TaskEntity("1", "Test Task 1", "In Progress", "This is a test task 1"),
                new TaskEntity("2", "Test Task 2", "Completed", "This is a test task 2")
        );

        when(taskService.findAll()).thenReturn(taskEntities);

        mockMvc.perform(get("/api/v1/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].title", is("Test Task 1")))
                .andExpect(jsonPath("$[0].status", is("In Progress")))
                .andExpect(jsonPath("$[0].description", is("This is a test task 1")))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].title", is("Test Task 2")))
                .andExpect(jsonPath("$[1].status", is("Completed")))
                .andExpect(jsonPath("$[1].description", is("This is a test task 2")));
    }



    @Test
    public void testFindByTitle() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setId("1");
        taskDto.setTitle("Test Task");
        taskDto.setStatus("In Progress");
        taskDto.setDescription("This is a test task");

        when(taskService.findByTitle("Test Task")).thenReturn(taskDto);

        mockMvc.perform(get("/api/v1/task/title/").param("title", "Test Task"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.status", is("In Progress")))
                .andExpect(jsonPath("$.description", is("This is a test task")));
    }

    @Test
    public void testFindById() throws Exception {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId("1");
        taskEntity.setTitle("Test Task");
        taskEntity.setStatus("In Progress");
        taskEntity.setDescription("This is a test task");
        TaskDto taskDto = new TaskDto();
        taskDto.setId(taskEntity.getId());
        taskDto.setTitle(taskEntity.getTitle());
        taskDto.setStatus(taskEntity.getStatus());
        taskDto.setDescription(taskEntity.getDescription());

        when(taskService.getById("1")).thenReturn(taskDto);

        mockMvc.perform(get("/api/v1/task/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.status", is("In Progress")))
                .andExpect(jsonPath("$.description", is("This is a test task")));
    }

    @Test
    public void testCreateTask() throws Exception {

            TaskDto taskDto = new TaskDto("1", "Test Task", "In Progress", "This is a test task");

            when(taskService.save(taskDto)).thenReturn(taskDto);

            mockMvc.perform(post("/api/v1/task/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"title\":\"Test Task\",\"status\":\"In Progress\",\"description\":\"This is a test task\"}"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is("1")))
                    .andExpect(jsonPath("$.title", is("Test Task")))
                    .andExpect(jsonPath("$.status", is("In Progress")))
                    .andExpect(jsonPath("$.description", is("This is a test task")));

    }

    @Test
    public void testUpdateTask() throws Exception {
        TaskDto taskDto = new TaskDto("1", "Test Task", "In Progress", "This is a test task");

        mockMvc.perform(put("/api/v1/task/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test Task\",\"status\":\"In Progress\",\"description\":\"This is a test task\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/v1/task/delete/1"))
                .andExpect(status().isOk());
    }
}


