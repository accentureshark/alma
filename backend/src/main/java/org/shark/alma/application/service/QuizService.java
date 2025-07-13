// backend/src/main/java/org/shark/alma/application/service/QuizService.java
package org.shark.alma.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.shark.alma.domain.model.QuizDefinition;
import org.shark.alma.domain.model.QuizDefinitionEntity;
import org.shark.alma.domain.model.QuizResponseRequest;
import org.shark.alma.domain.port.out.QuizRepository;
import org.shark.alma.domain.port.out.InferenceClient;
import org.shark.alma.llm.LlmService;
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
    private final LlmService llmService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuizService(QuizRepository quizRepository, InferenceClient inferenceClient, LlmService llmService) {
        this.quizRepository = quizRepository;
        this.inferenceClient = inferenceClient;
        this.llmService = llmService;
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
            entity.prompt = definition.getPrompt();
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
        def.setPrompt(entity.prompt);
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
        return inferenceClient.inferResult(request);
    }

    public String processResponseWithLlm(QuizResponseRequest request) {
        try {
            // Get the quiz definition to include questions context
            QuizDefinition quizDefinition = getDefinition(request.getDocumentId());
            
            // Build context with questions and answers
            StringBuilder context = new StringBuilder();
            context.append("Quiz: ").append(quizDefinition.getTema()).append("\n");
            context.append("Usuario: ").append(request.getUsuario()).append("\n\n");
            
            // Add questions and answers to context
            for (QuizDefinition.QuizStep step : quizDefinition.getSteps()) {
                context.append("Pregunta ").append(step.getStep()).append(": ").append(step.getTexto()).append("\n");
                if (step.getOpciones() != null && !step.getOpciones().isEmpty()) {
                    context.append("Opciones: ").append(String.join(", ", step.getOpciones())).append("\n");
                }
                String answer = request.getRespuestas().get(step.getId());
                context.append("Respuesta: ").append(answer != null ? answer : "Sin respuesta").append("\n\n");
            }
            
            // Use prompt from quiz definition first, then from request, then default
            String prompt = quizDefinition.getPrompt();
            if (prompt == null || prompt.trim().isEmpty()) {
                prompt = request.getCustomPrompt();
            }
            if (prompt == null || prompt.trim().isEmpty()) {
                prompt = "Analiza las respuestas del usuario al quiz y proporciona feedback constructivo. " +
                        "Evalúa la corrección de las respuestas y ofrece sugerencias de mejora cuando sea necesario.";
            }
            
            // Call LLM service
            return llmService.generate(prompt, context.toString());
            
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error procesando la respuesta con LLM: " + e.getMessage(), e);
        }
    }

    public List<String> listDocumentIds() {
        return quizRepository.findAll()
                .stream()
                .map(e -> e.documentId)
                .collect(Collectors.toList());
    }
}