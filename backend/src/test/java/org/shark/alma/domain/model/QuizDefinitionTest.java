package org.shark.alma.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.UUID;

class QuizDefinitionTest {

    @Test
    void testQuizDefinitionLanguageField() {
        // Test QuizDefinition domain model
        QuizDefinition quizDefinition = new QuizDefinition();
        quizDefinition.setDocumentId("test-quiz");
        quizDefinition.setTipo("quiz");
        quizDefinition.setTema("Test Topic");
        quizDefinition.setVersion("1.0");
        quizDefinition.setPrompt("Test prompt");
        quizDefinition.setLanguage("es");

        assertEquals("es", quizDefinition.getLanguage());
        assertEquals("test-quiz", quizDefinition.getDocumentId());
    }

    @Test
    void testQuizDefinitionEntityLanguageField() {
        // Test QuizDefinitionEntity JPA entity
        QuizDefinitionEntity entity = new QuizDefinitionEntity();
        entity.setId(UUID.randomUUID());
        entity.setDocumentId("test-quiz-entity");
        entity.setTipo("quiz");
        entity.setTema("Test Topic Entity");
        entity.setVersion("1.0");
        entity.setPrompt("Test prompt entity");
        entity.setLanguage("en");
        entity.setStepsJson("[]");
        entity.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

        assertEquals("en", entity.getLanguage());
        assertEquals("test-quiz-entity", entity.getDocumentId());
    }

    @Test
    void testQuizDefinitionLanguageCanBeNull() {
        // Test that language field can be null
        QuizDefinition quizDefinition = new QuizDefinition();
        quizDefinition.setLanguage(null);
        
        assertNull(quizDefinition.getLanguage());
    }
}