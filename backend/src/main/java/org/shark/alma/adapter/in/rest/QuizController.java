package org.shark.alma.adapter.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.shark.alma.application.service.QuizService;
import org.shark.alma.domain.model.QuizResponseRequest;
import org.shark.alma.domain.model.QuizDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@Tag(name = "Quiz Controller", description = "API para manejo de quizzes con inferencia de IA")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/upload")
    @Operation(summary = "Subir definición de quiz", description = "Guarda una nueva definición de quiz en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz guardado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<Void> uploadDefinition(@RequestBody QuizDefinition definition) {
        quizService.saveDefinition(definition);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{documentId}")
    @Operation(summary = "Obtener quiz por ID", description = "Recupera la definición de un quiz específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz encontrado"),
            @ApiResponse(responseCode = "404", description = "Quiz no encontrado")
    })
    public ResponseEntity<QuizDefinition> getQuiz(
            @Parameter(description = "ID del documento del quiz", required = true)
            @PathVariable String documentId) {
        return ResponseEntity.ok(quizService.getDefinition(documentId));
    }

    @PostMapping("/response")
    @Operation(summary = "Procesar respuesta de quiz", description = "Procesa una respuesta del usuario y genera una inferencia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta procesada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de respuesta inválidos")
    })
    public ResponseEntity<String> processResponse(@RequestBody QuizResponseRequest request) {
        String resultado = quizService.processResponse(request);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/response/llm")
    @Operation(summary = "Procesar respuesta de quiz con LLM", description = "Procesa una respuesta del usuario usando el servicio LLM local con prompt personalizado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Respuesta procesada exitosamente con LLM"),
            @ApiResponse(responseCode = "400", description = "Datos de respuesta inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<String> processResponseWithLlm(@RequestBody QuizResponseRequest request) {
        String resultado = quizService.processResponseWithLlm(request);
        return ResponseEntity.ok(resultado);
    }

    // backend/src/main/java/org/shark/alma/adapter/in/rest/QuizController.java
    @GetMapping("/list")
    @Operation(summary = "Listar todos los quizzes", description = "Obtiene una lista de todos los IDs de quizzes disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de quizzes obtenida exitosamente")
    public List<String> listAllQuizzes() {
        List<String> ids = quizService.listDocumentIds();
        return ids != null ? ids : List.of();
    }

    @PutMapping("/{documentId}")
    @Operation(summary = "Actualizar quiz", description = "Actualiza un quiz existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Quiz no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<QuizDefinition> updateQuiz(
            @Parameter(description = "ID del documento del quiz", required = true)
            @PathVariable String documentId,
            @RequestBody QuizDefinition definition) {
        QuizDefinition updated = quizService.updateDefinition(documentId, definition);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/step")
    @Operation(summary = "Obtener paso específico de quiz", description = "Recupera un paso específico de un quiz")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paso encontrado"),
            @ApiResponse(responseCode = "404", description = "Paso no encontrado")
    })
    public ResponseEntity<QuizDefinition.QuizStep> getStep(
            @Parameter(description = "ID del quiz", required = true)
            @RequestParam String quizId, 
            @Parameter(description = "Número del paso", required = true)
            @RequestParam int step) {
        QuizDefinition def = quizService.getDefinition(quizId);
        return def.getSteps().stream()
                .filter(s -> s.getStep() == step)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}

