package org.shark.alma.adapter.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shark.alma.application.service.QuizService;
import org.shark.alma.domain.model.QuizResponseRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/quiz/submission")
@Tag(name = "Quiz Submission Controller", description = "API para el envío y procesamiento de respuestas de quiz")
public class QuizSubmissionController {

    private static final Logger logger = LoggerFactory.getLogger(QuizSubmissionController.class);

    private final QuizService quizService;

    public QuizSubmissionController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/submit")
    @Operation(summary = "Enviar respuestas de quiz", description = "Procesa las respuestas del usuario y genera análisis con LLM")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuestas procesadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de respuesta inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> submitQuiz(@RequestBody QuizResponseRequest request) {
        logger.info("Procesando envío de quiz para documentId: {}, usuario: {}", 
                   request.getDocumentId(), request.getUsuario());
        
        try {
            // Process the quiz submission using the LLM service
            String resultado = quizService.processResponseWithLlm(request);
            
            logger.info("Quiz procesado exitosamente para documentId: {}", request.getDocumentId());
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            logger.error("Error procesando quiz submission para documentId: {}", request.getDocumentId(), e);
            throw e;
        }
    }
}