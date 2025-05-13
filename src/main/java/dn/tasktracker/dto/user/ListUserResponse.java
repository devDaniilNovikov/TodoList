package dn.tasktracker.dto.user;


import dn.tasktracker.entity.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ListUserResponse {

    private Page<UserEntity> users;

    public ListUserResponse(List<UserEntity> userResponses,int totalElements){
        this.users = new PageImpl<>(new ArrayList<>(userResponses));
    }
}
