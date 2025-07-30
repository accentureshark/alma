package org.shark.alma.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.confirm-url:http://localhost:8082/api/auth/confirm?token=}")
    private String confirmUrl;

    public void sendConfirmationEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirmación de cuenta");
        message.setText("Para confirmar tu cuenta haz clic en el siguiente enlace: " + confirmUrl + token);
        mailSender.send(message);
    }
}
