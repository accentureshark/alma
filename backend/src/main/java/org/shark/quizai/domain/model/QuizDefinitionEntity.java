package org.shark.quizai.domain.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "quiz_definition", schema = "quiz")
public class QuizDefinitionEntity {
    @Id
    @Column(columnDefinition = "uuid")
    public UUID id;

    @Column(name = "document_id", unique = true, nullable = false)
    public String documentId;

    public String tipo;
    public String tema;
    public String version;

    @Column(name = "steps_json", columnDefinition = "jsonb", nullable = false)
    public String stepsJson;

    @Column(name = "created_at")
    public java.sql.Timestamp createdAt;
}