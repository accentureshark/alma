package org.shark.alma.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private boolean administrador;
    
    public static UserDto fromUser(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.isAdministrador());
    }
}