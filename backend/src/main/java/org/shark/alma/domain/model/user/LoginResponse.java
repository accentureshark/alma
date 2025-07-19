package org.shark.alma.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private UserDto user;
    
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.user = null;
    }
}