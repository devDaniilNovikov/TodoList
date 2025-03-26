package dn.tasktracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    private String id;

    private String title;

    private String description;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd-HH-mm")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd-HH-mm")
    private LocalDateTime updatedAt;

    @JsonIgnore
    private Integer pageNumber;

    @JsonIgnore
    private Integer pageSize;

    public TaskDto(String number, String s, String inProgress, String s1) {
    }

    public IntStream threadLocalRandom() {
        return ThreadLocalRandom.current().ints(10000000);
    }




}
