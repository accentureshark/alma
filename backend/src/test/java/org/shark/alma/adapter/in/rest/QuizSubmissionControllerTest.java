package org.shark.alma.adapter.in.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.shark.alma.application.service.QuizService;
import org.shark.alma.application.service.QuizResultService;
import org.shark.alma.domain.model.QuizResponseRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuizSubmissionControllerTest {

    @Mock
    private QuizService quizService;

    @Mock
    private QuizResultService quizResultService;

    private QuizSubmissionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new QuizSubmissionController(quizService, quizResultService);
    }

    @Test
    void testSubmitQuiz_Success() {
        // Arrange
        QuizResponseRequest request = new QuizResponseRequest();
        request.setDocumentId("quiz-123");
        request.setUsuario("test-user");
        Map<String, String> respuestas = new HashMap<>();
        respuestas.put("step1", "París");
        respuestas.put("step2", "Pacífico");
        request.setRespuestas(respuestas);
        request.setCustomPrompt("Analiza las respuestas");

        String expectedResponse = "Análisis: Las respuestas son correctas";
        when(quizService.processResponseWithLlm(request)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<String> response = controller.submitQuiz(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(quizService, times(1)).processResponseWithLlm(request);
    }

    @Test
    void testSubmitQuiz_ServiceException() {
        // Arrange
        QuizResponseRequest request = new QuizResponseRequest();
        request.setDocumentId("quiz-123");
        request.setUsuario("test-user");
        request.setRespuestas(new HashMap<>());

        when(quizService.processResponseWithLlm(request)).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.submitQuiz(request));
        verify(quizService, times(1)).processResponseWithLlm(request);
    }
}