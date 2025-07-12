// backend/src/main/java/org/shark/alma/application/service/QuizService.java
package org.shark.alma.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.shark.alma.domain.model.QuizDefinition;
import org.shark.alma.domain.model.QuizDefinitionEntity;
import org.shark.alma.domain.model.QuizResponseRequest;
import org.shark.alma.domain.port.out.QuizRepository;
import org.shark.alma.domain.port.out.InferenceClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final InferenceClient inferenceClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuizService(QuizRepository quizRepository, InferenceClient inferenceClient) {
        this.quizRepository = quizRepository;
        this.inferenceClient = inferenceClient;
    }

    public void saveDefinition(QuizDefinition definition) {
        if (definition == null) {
            quizRepository.save(null);
            return;
        }
        try {
            QuizDefinitionEntity entity = new QuizDefinitionEntity();
            entity.id = UUID.randomUUID();
            entity.documentId = definition.getDocumentId();
            entity.tipo = definition.getTipo();
            entity.tema = definition.getTema();
            entity.version = definition.getVersion();
            entity.stepsJson = objectMapper.writeValueAsString(definition.getSteps());
            entity.createdAt = new java.sql.Timestamp(System.currentTimeMillis());
            quizRepository.save(entity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar el quiz", e);
        }
    }

    public QuizDefinition getDefinition(String documentId) {
        Optional<QuizDefinitionEntity> entityOpt = quizRepository.findByDocumentId(documentId);
        QuizDefinitionEntity entity = entityOpt.orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz no encontrado")
        );
        QuizDefinition def = new QuizDefinition();
        def.setDocumentId(entity.documentId);
        def.setTipo(entity.tipo);
        def.setTema(entity.tema);
        def.setVersion(entity.version);
        try {
            def.setSteps(objectMapper.readValue(
                entity.stepsJson,
                new TypeReference<List<QuizDefinition.QuizStep>>() {})
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al leer los pasos del quiz", e);
        }
        return def;
    }

    public String processResponse(QuizResponseRequest request) {
        // Convert QuizResponseRequest to a text prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Evalúa las siguientes respuestas del quiz:\n");
        promptBuilder.append("Usuario: ").append(request.getUsuario()).append("\n");
        promptBuilder.append("Documento ID: ").append(request.getDocumentId()).append("\n");
        
        if (request.getRespuestas() != null && !request.getRespuestas().isEmpty()) {
            promptBuilder.append("Respuestas:\n");
            request.getRespuestas().forEach((pregunta, respuesta) -> 
                promptBuilder.append("- ").append(pregunta).append(": ").append(respuesta).append("\n")
            );
        }
        
        if (request.getCustomPrompt() != null && !request.getCustomPrompt().trim().isEmpty()) {
            promptBuilder.append("\nInstrucciones adicionales: ").append(request.getCustomPrompt());
        }
        
        return inferenceClient.generateResponse(promptBuilder.toString());
    }

    public List<String> listDocumentIds() {
        return quizRepository.findAll()
                .stream()
                .map(e -> e.documentId)
                .collect(Collectors.toList());
    }
}