package dn.tasktracker.web.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "ChangePassword",description = "ДТО для смены пароля")
public class ChangePasswordDto {

    @NotNull
    @Schema(name = "oldPassword",description = "Старый пароль")
    private String oldPassword;

    @NotNull
    @Schema(name = "newPassword",description = "Новый пароль")
    private String newPassword;
}
