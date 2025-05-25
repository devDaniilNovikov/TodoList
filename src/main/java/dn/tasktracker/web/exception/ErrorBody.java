package dn.tasktracker.web.exception;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorBody {
    private int statusCode;
    private String description;
    private String path;
}
