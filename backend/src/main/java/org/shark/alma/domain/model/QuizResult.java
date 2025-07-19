package org.shark.alma.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizResult {
    private UUID id;
    private String quizDocumentId;
    private String usuario;
    private Map<String, String> respuestas;
    private String resultadoInferencia;
    private BigDecimal calificacion;
    private LocalDateTime fecha;
    private String quizTema; // For display purposes, filled from quiz definition
}