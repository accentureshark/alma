package org.shark.alma.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "quiz_definition", schema = "quiz")
public class QuizDefinitionEntity {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "document_id", unique = true, nullable = false, columnDefinition = "TEXT")
    private String documentId;

    @Column(name = "tipo", columnDefinition = "TEXT")
    private String tipo;

    @Column(name = "tema", columnDefinition = "TEXT")
    private String tema;

    @Column(name = "version", columnDefinition = "TEXT")
    private String version;

    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "steps_json", columnDefinition = "TEXT", nullable = false)
    private String stepsJson;

    @Column(name = "created_at")
    private java.sql.Timestamp createdAt;
}