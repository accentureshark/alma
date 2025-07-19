package org.shark.alma.application.service;

import org.shark.alma.domain.model.user.User;
import org.shark.alma.domain.port.out.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt;
        }
        return Optional.empty();
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.trim());
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Nuevo método para comparar contraseñas en texto plano
    public boolean checkPassword(User user, String rawPassword) {
        return user.getPassword().equals(rawPassword);
    }
}