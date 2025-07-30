package org.shark.alma.adapter.in.rest;

import org.shark.alma.application.service.UserService;
import org.shark.alma.application.service.RegistrationService;
import org.shark.alma.domain.model.user.LoginRequest;
import org.shark.alma.domain.model.user.LoginResponse;
import org.shark.alma.domain.model.user.User;
import org.shark.alma.domain.model.user.UserDto;
import org.shark.alma.domain.model.user.RegistrationRequest;
import org.shark.alma.domain.model.user.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail().trim();
        String password = loginRequest.getPassword().trim();
        logger.info("Intentando login para email: [{}]", email);
        logger.info("Intentando login para password: [{}]", password);
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            logger.warn("Login fallido: usuario no encontrado para email: [{}]", email);
            return ResponseEntity.status(401).body(new LoginResponse(false, "Invalid credentials"));
        }

        User user = userOpt.get();
        boolean passwordOk = userService.checkPassword(user, password);
        if (!passwordOk) {
            logger.warn("Login fallido: contraseña incorrecta para email: {}. Contraseña ingresada: {}", email, password);
            logger.info("Contraseña real (hash): {}", user.getPassword());
            return ResponseEntity.status(401).body(new LoginResponse(false, "Invalid credentials"));
        }

        logger.info("Login exitoso para email: {}", email);
        UserDto userDto = UserDto.fromUser(user);
        return ResponseEntity.ok(new LoginResponse(true, "Login successful", userDto));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        logger.info("Buscando usuario por email: {}", email);
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            logger.info("Usuario encontrado: {}", email);
            return ResponseEntity.ok(UserDto.fromUser(userOpt.get()));
        } else {
            logger.warn("Usuario no encontrado: {}", email);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponse> register(@RequestBody RegistrationRequest request) {
        try {
            registrationService.register(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new GenericResponse(true, "Registro iniciado. Revisa tu correo."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new GenericResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<GenericResponse> confirm(@RequestParam String token) {
        boolean ok = registrationService.confirm(token);
        if (ok) {
            return ResponseEntity.ok(new GenericResponse(true, "Usuario confirmado"));
        } else {
            return ResponseEntity.badRequest().body(new GenericResponse(false, "Token inválido"));
        }
    }
}