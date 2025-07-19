package org.shark.alma.adapter.in.rest;

import org.shark.alma.application.service.UserService;
import org.shark.alma.domain.model.user.LoginRequest;
import org.shark.alma.domain.model.user.LoginResponse;
import org.shark.alma.domain.model.user.User;
import org.shark.alma.domain.model.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserDto userDto = UserDto.fromUser(user);
            return ResponseEntity.ok(new LoginResponse(true, "Login successful", userDto));
        } else {
            return ResponseEntity.status(401).body(new LoginResponse(false, "Invalid credentials"));
        }
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(UserDto.fromUser(userOpt.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}