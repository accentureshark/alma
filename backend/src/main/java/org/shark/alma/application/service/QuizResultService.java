package org.shark.alma.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.shark.alma.domain.model.QuizDefinition;
import org.shark.alma.domain.model.QuizResult;
import org.shark.alma.domain.model.QuizResultEntity;
import org.shark.alma.domain.port.out.QuizResultRepository;
import org.shark.alma.domain.port.out.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class QuizResultService {

    private static final Logger logger = LoggerFactory.getLogger(QuizResultService.class);
    private final QuizResultRepository quizResultRepository;
    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public QuizResultService(QuizResultRepository quizResultRepository, QuizRepository quizRepository) {
        this.quizResultRepository = quizResultRepository;
        this.quizRepository = quizRepository;
    }

    public QuizResult saveQuizResult(String quizDocumentId, String usuario, Map<String, String> respuestas, String resultadoInferencia, BigDecimal calificacion) {
        try {
            QuizResultEntity entity = QuizResultEntity.builder()
                    .id(UUID.randomUUID())
                    .quizDocumentId(quizDocumentId)
                    .usuario(usuario)
                    .respuestas(objectMapper.writeValueAsString(respuestas))
                    .resultadoInferencia(resultadoInferencia)
                    .calificacion(calificacion)
                    .createdAt(new java.sql.Timestamp(System.currentTimeMillis()))
                    .build();

            QuizResultEntity saved = quizResultRepository.save(entity);
            logger.info("Quiz result saved with ID: {} for user: {} and quiz: {}", saved.getId(), usuario, quizDocumentId);
            
            return convertToDto(saved);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing quiz responses", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving quiz result", e);
        }
    }

    public List<QuizResult> getAllQuizResults() {
        return quizResultRepository.findAllOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<QuizResult> getQuizResultsByUser(String usuario) {
        return quizResultRepository.findByUsuarioOrderByCreatedAtDesc(usuario)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<QuizResult> getQuizResultsByQuiz(String quizDocumentId) {
        return quizResultRepository.findByQuizDocumentIdOrderByCreatedAtDesc(quizDocumentId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private QuizResult convertToDto(QuizResultEntity entity) {
        try {
            Map<String, String> respuestas = objectMapper.readValue(entity.getRespuestas(), new TypeReference<Map<String, String>>() {});
            
            // Try to get quiz tema for display purposes
            String quizTema = null;
            try {
                var quizEntityOpt = quizRepository.findByDocumentId(entity.getQuizDocumentId());
                if (quizEntityOpt.isPresent()) {
                    quizTema = quizEntityOpt.get().getTema();
                } else {
                    quizTema = entity.getQuizDocumentId(); // fallback to document ID
                }
            } catch (Exception e) {
                logger.warn("Could not load quiz definition for display: {}", entity.getQuizDocumentId());
                quizTema = entity.getQuizDocumentId(); // fallback to document ID
            }

            return QuizResult.builder()
                    .id(entity.getId())
                    .quizDocumentId(entity.getQuizDocumentId())
                    .usuario(entity.getUsuario())
                    .respuestas(respuestas)
                    .resultadoInferencia(entity.getResultadoInferencia())
                    .calificacion(entity.getCalificacion())
                    .fecha(entity.getCreatedAt().toLocalDateTime())
                    .quizTema(quizTema)
                    .build();
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing quiz responses for entity ID: {}", entity.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading quiz result", e);
        }
    }

    /**
     * Calculate a basic score based on the LLM result
     * This is a simple implementation that could be enhanced based on business requirements
     */
    public BigDecimal calculateScore(String resultadoInferencia) {
        if (resultadoInferencia == null || resultadoInferencia.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Simple scoring logic - could be enhanced
        String resultado = resultadoInferencia.toLowerCase();
        
        // Look for positive indicators in the LLM response
        int positiveScore = 0;
        
        if (resultado.contains("tech expert")) positiveScore += 2;
        if (resultado.contains("tech manager")) positiveScore += 2;
        if (resultado.contains("recomendación")) positiveScore += 1;
        if (resultado.contains("justificación")) positiveScore += 1;
        if (resultado.contains("alinea")) positiveScore += 1;
        
        // Basic scoring out of 10
        BigDecimal baseScore = BigDecimal.valueOf(Math.min(10.0, positiveScore * 1.5));
        
        return baseScore;
    }
}