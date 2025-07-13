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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

@Service
public class QuizService {


    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);
    @Value("${llm.prompts.default}")
    private String defaultPrompt;

    ///
    /// [{"id": "preferencia", "step": 1, "texto": "¿Qué disfrutas más en tu día a día de trabajo?", "opciones": ["Resolver problemas técnicos complejos", "Facilitar que el equipo alcance sus objetivos", "Pensar en arquitectura y decisiones a largo plazo", "Acompañar el crecimiento de otras personas"]}, {"id": "motivación", "step": 2, "texto": "¿Qué te motiva más?", "opciones": ["Aprender nuevas tecnologías", "Que el equipo funcione con eficiencia", "Resolver conflictos y remover impedimentos", "Ver el impacto del producto en el negocio"]}, {"id": "toma_decisiones", "step": 3, "texto": "¿Te sentís cómodo tomando decisiones difíciles que afectan al equipo?", "opciones": ["Prefiero evitarlo, no es mi rol", "Si hay datos y contexto, sí", "No siempre, pero me esfuerzo en aprenderlo", "Sí, lo considero parte natural del liderazgo"]}, {"id": "liderazgo", "step": 4, "texto": "¿Cómo te sentís cuando tenés que liderar reuniones de equipo?", "opciones": ["Incómodo, prefiero que otros lo hagan", "Lo hago si es necesario, pero no me entusiasma", "Me gusta facilitar el diálogo y organizar ideas", "Disfruto ese tipo de situaciones, me sale natural"]}, {"id": "empatia", "step": 5, "texto": "¿Qué tan importante es para vos el bienestar del equipo?", "opciones": ["No es mi prioridad, eso lo maneja alguien más", "Importa, pero no más que entregar código", "Es clave para que el equipo funcione bien", "Es una de mis principales preocupaciones"]}, {"id": "aprendizaje", "step": 6, "texto": "¿Cómo manejás situaciones donde tenés que aprender una nueva tecnología?", "opciones": ["Investigo y pruebo por mi cuenta", "Pido ayuda a alguien que sepa más", "Armo un plan para que el equipo la entienda", "Organizo una capacitación o lo comparto en comunidad"]}, {"id": "delegación", "step": 7, "texto": "¿Qué tan cómodo te sentís delegando tareas técnicas a otros?", "opciones": ["Prefiero hacer todo yo para asegurar calidad", "Delego solo si confío mucho en la persona", "Me esfuerzo por delegar para balancear carga", "Confío y uso eso para empoderar al equipo"]}, {"id": "seguridad", "step": 8, "texto": "¿Qué tan seguro te sentís tomando decisiones técnicas importantes sin consultar a otros?", "opciones": ["Muy inseguro, siempre consulto", "Solo decido si estoy 100% seguro", "Prefiero consensuar antes de decidir", "Me siento capaz, pero valoro el input del equipo"]}, {"id": "resolución_problemas", "step": 9, "texto": "Cuando te enfrentás a un problema técnico, ¿qué preferís?", "opciones": ["Resolverlo solo, a fondo", "Proponer ideas y abrir la discusión", "Hacer pairing o mob programming", "Repartirlo en el equipo para resolver más rápido"]}, {"id": "relaciones_laborales", "step": 10, "texto": "¿Qué lugar ocupan para vos las conversaciones 1:1 con colegas o líderes?", "opciones": ["No me interesan mucho", "Me sirven si hay algo técnico para discutir", "Me ayudan a crecer profesionalmente", "Son esenciales para construir relaciones sanas"]}]
    ///
    ///
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

        logger.info("Entity desde DB: documentId={}, stepsJson={}", entity.documentId, entity.stepsJson);

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
                prompt = getDefaultPrompt();
            }
            
            // Call LLM service
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
        // Verify the quiz exists
        QuizDefinition existing = getDefinition(documentId);

        // Update the definition
        definition.setDocumentId(documentId);

        try {
            // Find and update the existing entity
            Optional<QuizDefinitionEntity> entityOpt = quizRepository.findByDocumentId(documentId);
            if (entityOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz no encontrado");
            }

            QuizDefinitionEntity entity = entityOpt.get();
            entity.tipo = definition.getTipo();
            entity.tema = definition.getTema();
            entity.version = definition.getVersion();
            entity.prompt = definition.getPrompt();
            entity.stepsJson = objectMapper.writeValueAsString(definition.getSteps());
            entity.createdAt = new java.sql.Timestamp(System.currentTimeMillis());

            quizRepository.save(entity);

            return definition;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar el quiz", e);
        }
    }

    public List<String> listDocumentIds() {
        return quizRepository.findAll()
                .stream()
                .map(e -> e.documentId)
                .collect(Collectors.toList());
    }
}