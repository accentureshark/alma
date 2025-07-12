package org.shark.quizai.domain.port.out;

import org.shark.quizai.domain.model.QuizDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<QuizDefinitionEntity, java.util.UUID> {
    Optional<QuizDefinitionEntity> findByDocumentId(String documentId);
}