package org.shark.alma.domain.model.user;

import lombok.Data;

@Data
public class RegistrationRequest {
    private String email;
    private String password;
}
