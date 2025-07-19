package org.shark.alma.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "quiz_response", schema = "quiz")
public class QuizResultEntity {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "quiz_document_id", nullable = false, columnDefinition = "TEXT")
    private String quizDocumentId;

    @Column(name = "usuario", nullable = false, columnDefinition = "TEXT")
    private String usuario;

    @Column(name = "respuestas", columnDefinition = "TEXT", nullable = false)
    private String respuestas;

    @Column(name = "resultado_inferencia", columnDefinition = "TEXT")
    private String resultadoInferencia;

    @Column(name = "calificacion", precision = 5, scale = 2)
    private BigDecimal calificacion;

    @Column(name = "created_at")
    private java.sql.Timestamp createdAt;
}