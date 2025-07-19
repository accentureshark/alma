package org.shark.alma.domain.port.out;

import org.shark.alma.domain.model.QuizResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QuizResultRepository extends JpaRepository<QuizResultEntity, UUID> {
    
    List<QuizResultEntity> findByUsuarioOrderByCreatedAtDesc(String usuario);
    
    List<QuizResultEntity> findByQuizDocumentIdOrderByCreatedAtDesc(String quizDocumentId);
    
    @Query("SELECT qr FROM QuizResultEntity qr ORDER BY qr.createdAt DESC")
    List<QuizResultEntity> findAllOrderByCreatedAtDesc();
    
    @Query("SELECT qr FROM QuizResultEntity qr WHERE qr.usuario = :usuario AND qr.quizDocumentId = :quizDocumentId ORDER BY qr.createdAt DESC")
    List<QuizResultEntity> findByUsuarioAndQuizDocumentIdOrderByCreatedAtDesc(@Param("usuario") String usuario, @Param("quizDocumentId") String quizDocumentId);
}