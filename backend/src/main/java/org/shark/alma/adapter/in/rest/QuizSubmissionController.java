package org.shark.alma.adapter.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shark.alma.application.service.QuizService;
import org.shark.alma.application.service.QuizResultService;
import org.shark.alma.domain.model.QuizResponseRequest;
import org.shark.alma.domain.model.QuizResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/quiz/submission")
@Tag(name = "Quiz Submission Controller", description = "API para el envío y procesamiento de respuestas de quiz y gestión de resultados")
public class QuizSubmissionController {

    private static final Logger logger = LoggerFactory.getLogger(QuizSubmissionController.class);

    private final QuizService quizService;
    private final QuizResultService quizResultService;

    public QuizSubmissionController(QuizService quizService, QuizResultService quizResultService) {
        this.quizService = quizService;
        this.quizResultService = quizResultService;
    }

    @PostMapping("/submit")
    @Operation(summary = "Enviar respuestas de quiz", description = "Procesa las respuestas del usuario y genera análisis con LLM, persistiendo el resultado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuestas procesadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de respuesta inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> submitQuiz(@RequestBody QuizResponseRequest request) {
        logger.info("Procesando envío de quiz para documentId: {}, usuario: {}", 
                   request.getDocumentId(), request.getUsuario());
        
        try {
            // Process the quiz submission using the LLM service (this now also saves the result)
            String resultado = quizService.processResponseWithLlm(request);
            
            logger.info("Quiz procesado y resultado guardado exitosamente para documentId: {}", request.getDocumentId());
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            logger.error("Error procesando quiz submission para documentId: {}", request.getDocumentId(), e);
            throw e;
        }
    }

    @GetMapping("/results")
    @Operation(summary = "Obtener todos los resultados de quiz (Admin)", description = "Obtiene todos los resultados de quiz para administradores")
    @ApiResponse(responseCode = "200", description = "Resultados obtenidos exitosamente")
    public ResponseEntity<List<QuizResult>> getAllResults() {
        logger.info("Obteniendo todos los resultados de quiz");
        List<QuizResult> results = quizResultService.getAllQuizResults();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/user/{usuario}")
    @Operation(summary = "Obtener resultados por usuario", description = "Obtiene todos los resultados de quiz de un usuario específico")
    @ApiResponse(responseCode = "200", description = "Resultados del usuario obtenidos exitosamente")
    public ResponseEntity<List<QuizResult>> getResultsByUser(
            @Parameter(description = "Nombre del usuario", required = true)
            @PathVariable String usuario) {
        logger.info("Obteniendo resultados de quiz para usuario: {}", usuario);
        List<QuizResult> results = quizResultService.getQuizResultsByUser(usuario);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/quiz/{quizDocumentId}")
    @Operation(summary = "Obtener resultados por quiz", description = "Obtiene todos los resultados de un quiz específico")
    @ApiResponse(responseCode = "200", description = "Resultados del quiz obtenidos exitosamente")
    public ResponseEntity<List<QuizResult>> getResultsByQuiz(
            @Parameter(description = "ID del documento del quiz", required = true)
            @PathVariable String quizDocumentId) {
        logger.info("Obteniendo resultados para quiz: {}", quizDocumentId);
        List<QuizResult> results = quizResultService.getQuizResultsByQuiz(quizDocumentId);
        return ResponseEntity.ok(results);
    }
}