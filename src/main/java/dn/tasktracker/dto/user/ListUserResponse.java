package dn.tasktracker.dto.user;


import dn.tasktracker.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(name = "ListUserResponse",description = "Список пользователей с применением пагинации")
public class ListUserResponse {

    @Schema(name = "users",description = "Список пользователей")
    private List<UserResponse> users = new ArrayList<>();

}
