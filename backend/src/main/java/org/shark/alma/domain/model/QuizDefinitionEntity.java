package org.shark.alma.domain.model;

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
    public String prompt;

    @Column(name = "steps_json", columnDefinition = "jsonb", nullable = false)
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    public String stepsJson;

    @Column(name = "created_at")
    public java.sql.Timestamp createdAt;
}