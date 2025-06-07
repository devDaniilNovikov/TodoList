package dn.tasktracker.web.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(name = "ListUserResponse",description = "Список пользователей с применением пагинации")
public class ListUserResponse implements Serializable {

    @Schema(name = "users",description = "Список пользователей")
    private List<UserResponse> users = new ArrayList<>();

}
