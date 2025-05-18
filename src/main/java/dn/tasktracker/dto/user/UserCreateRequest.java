package dn.tasktracker.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UserCreate",description = "ДТО для создания пользователя")
public class UserCreateRequest {

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 3, max = 15, message = "Имя должно быть не меньше 3 и не больше 15 символов")
    @Schema(name = "username",description = "Имя пользователя")
    private String username;
    
    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 8, max = 20, message = "Пароль должен быть не меньше 8 и не больше 20 символов")
    @Schema(name = "password",description = "Пароль пользователя")
    private String password;

    @NotBlank(message = "Email не должен быть пустым")
    @Schema(name = "email",description = "E-mail пользователя")
    private String email;

    @Nullable
    @Schema(name = "phoneNumber",description = "Номер телефона пользователя")
    private String phoneNumber;

}
