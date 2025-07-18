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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Service
public class QuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
    @Value("${llm.prompts.default}")
    private String defaultPrompt;

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
            entity.setId(UUID.randomUUID());
            entity.setDocumentId(definition.getDocumentId());
            entity.setTipo(definition.getTipo());
            entity.setTema(definition.getTema());
            entity.setVersion(definition.getVersion());
            entity.setPrompt(definition.getPrompt());
            entity.setStepsJson(objectMapper.writeValueAsString(definition.getSteps()));
            entity.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
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

        logger.info("Entity desde DB: documentId={}, stepsJson={}", entity.getDocumentId(), entity.getStepsJson());

        QuizDefinition def = new QuizDefinition();
        def.setDocumentId(entity.getDocumentId());
        def.setTipo(entity.getTipo());
        def.setTema(entity.getTema());
        def.setVersion(entity.getVersion());
        def.setPrompt(entity.getPrompt());
        try {
            def.setSteps(objectMapper.readValue(
                    entity.getStepsJson(),
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
            QuizDefinition quizDefinition = getDefinition(request.getDocumentId());

            StringBuilder context = new StringBuilder();
            context.append("Quiz: ").append(quizDefinition.getTema()).append("\n");
            context.append("Usuario: ").append(request.getUsuario()).append("\n\n");

            for (QuizDefinition.QuizStep step : quizDefinition.getSteps()) {
                context.append("Pregunta ").append(step.getStep()).append(": ").append(step.getTexto()).append("\n");
                if (step.getOpciones() != null && !step.getOpciones().isEmpty()) {
                    context.append("Opciones: ").append(String.join(", ", step.getOpciones())).append("\n");
                }
                String answer = request.getRespuestas().get(step.getId());
                context.append("Respuesta: ").append(answer != null ? answer : "Sin respuesta").append("\n\n");
            }

            String prompt = quizDefinition.getPrompt();
            if (prompt == null || prompt.trim().isEmpty()) {
                prompt = request.getCustomPrompt();
            }
            if (prompt == null || prompt.trim().isEmpty()) {
                prompt = getDefaultPrompt();
            }

            return llmService.generate(prompt, context.toString());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error procesando la respuesta con LLM: " + e.getMessage(), e);
        }
    }

    public String getDefaultPrompt() {
        return defaultPrompt;
    }

    public QuizDefinition updateDefinition(String documentId, QuizDefinition definition) {
        QuizDefinition existing = getDefinition(documentId);

        definition.setDocumentId(documentId);

        try {
            Optional<QuizDefinitionEntity> entityOpt = quizRepository.findByDocumentId(documentId);
            if (entityOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz no encontrado");
            }

            QuizDefinitionEntity entity = entityOpt.get();
            entity.setTipo(definition.getTipo());
            entity.setTema(definition.getTema());
            entity.setVersion(definition.getVersion());
            entity.setPrompt(definition.getPrompt());
            entity.setStepsJson(objectMapper.writeValueAsString(definition.getSteps()));
            entity.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

            quizRepository.save(entity);

            return definition;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el quiz", e);
        }
    }

    public List<String> listDocumentIds() {
        return quizRepository.findAll()
                .stream()
                .map(e -> e.getDocumentId())
                .collect(Collectors.toList());
    }
}