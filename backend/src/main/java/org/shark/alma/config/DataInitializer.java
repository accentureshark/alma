package org.shark.alma.config;

import org.shark.alma.application.service.UserService;
import org.shark.alma.domain.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Check if users already exist
        if (userService.findByEmail("admin@alma.com").isEmpty()) {
            // Create admin user
            User admin = User.builder()
                    .email("admin@alma.com")
                    .password("admin123")
                    .administrador(true)
                    .build();
            userService.saveUser(admin);
            System.out.println("Created admin user: admin@alma.com / admin123");
        }

        if (userService.findByEmail("user@alma.com").isEmpty()) {
            // Create regular user
            User user = User.builder()
                    .email("user@alma.com")
                    .password("user123")
                    .administrador(false)
                    .build();
            userService.saveUser(user);
            System.out.println("Created user: user@alma.com / user123");
        }

        if (userService.findByEmail("test@alma.com").isEmpty()) {
            // Create test user
            User test = User.builder()
                    .email("test@alma.com")
                    .password("test123")
                    .administrador(false)
                    .build();
            userService.saveUser(test);
            System.out.println("Created test user: test@alma.com / test123");
        }
    }
}