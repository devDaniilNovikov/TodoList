package dn.tasktracker.dto.user;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 3, max = 15, message = "Имя должно быть не меньше 3 и не больше 15 символов")
    private String username;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 8, max = 20, message = "Пароль должен быть не меньше 8 и не больше 20 символов")
    private String password;

    @Nullable
    private String phoneNumber;

}
