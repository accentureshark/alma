package org.shark.alma.domain.port.out;

import org.shark.alma.domain.model.QuizDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<QuizDefinitionEntity, java.util.UUID> {
    Optional<QuizDefinitionEntity> findByDocumentId(String documentId);
}