package org.shark.alma.domain.model.user;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}