package org.shark.alma.application.service;

import org.shark.alma.domain.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RegistrationService {

    private final Map<String, User> pendingUsers = new ConcurrentHashMap<>();

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    public String register(String email, String password) {
        if (!email.toLowerCase().endsWith("@accenture.com")) {
            throw new IllegalArgumentException("Solo se permiten emails de accenture.com");
        }
        String token = UUID.randomUUID().toString();
        User user = User.builder()
                .email(email.trim())
                .password(password.trim())
                .administrador(false)
                .build();
        pendingUsers.put(token, user);
        emailService.sendConfirmationEmail(email, token);
        return token;
    }

    public boolean confirm(String token) {
        User user = pendingUsers.remove(token);
        if (user == null) {
            return false;
        }
        userService.saveUser(user);
        return true;
    }
}
