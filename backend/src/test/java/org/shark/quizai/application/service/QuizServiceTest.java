
package org.shark.alma.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shark.alma.domain.model.QuizDefinition;
import org.shark.alma.domain.model.QuizDefinitionEntity;
import org.shark.alma.domain.model.QuizResponseRequest;
import org.shark.alma.domain.port.out.InferenceClient;
import org.shark.alma.domain.port.out.QuizRepository;
import org.shark.alma.llm.LlmService;

import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private InferenceClient inferenceClient;

    @Mock
    private LlmService llmService;

    @InjectMocks
    private QuizService quizService;

    private QuizDefinition testQuizDefinition;
    private QuizDefinitionEntity testQuizEntity;
    private QuizResponseRequest testQuizResponse;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        QuizDefinition.QuizStep step1 = new QuizDefinition.QuizStep(
                1, "step1", "¿Cuál es la capital de Francia?",
                Arrays.asList("Madrid", "París", "Londres", "Roma")
        );
        QuizDefinition.QuizStep step2 = new QuizDefinition.QuizStep(
                2, "step2", "¿Cuál es el océano más grande?",
                Arrays.asList("Atlántico", "Índico", "Pacífico", "Ártico")
        );

        testQuizDefinition = new QuizDefinition(
                "quiz-123", "educativo", "geografía", "1.0", "Default prompt for testing",
                Arrays.asList(step1, step2)
        );

        testQuizEntity = new QuizDefinitionEntity();
        testQuizEntity.id = UUID.randomUUID();
        testQuizEntity.documentId = "quiz-123";
        testQuizEntity.tipo = "educativo";
        testQuizEntity.tema = "geografía";
        testQuizEntity.version = "1.0";
        testQuizEntity.prompt = "Default prompt for testing";
        testQuizEntity.stepsJson = objectMapper.writeValueAsString(Arrays.asList(step1, step2));
        testQuizEntity.createdAt = new Timestamp(System.currentTimeMillis());

        Map<String, String> respuestas = new HashMap<>();
        respuestas.put("step1", "París");
        respuestas.put("step2", "Pacífico");

        testQuizResponse = new QuizResponseRequest();
        testQuizResponse.setDocumentId("quiz-123");
        testQuizResponse.setUsuario("testUser");
        testQuizResponse.setRespuestas(respuestas);
        testQuizResponse.setQuery("Evalúa mis respuestas");
        testQuizResponse.setConversationId("conv-123");
        testQuizResponse.setCustomPrompt("Proporciona feedback detallado");
    }

    @Test
    void saveDefinition_ShouldCallRepository() {
        // Act
        quizService.saveDefinition(testQuizDefinition);

        // Assert
        verify(quizRepository, times(1)).save(any(QuizDefinitionEntity.class));
    }

    @Test
    void getDefinition_WhenQuizExists_ShouldReturnQuiz() {
        // Arrange
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));

        // Act
        QuizDefinition result = quizService.getDefinition("quiz-123");

        // Assert
        assertNotNull(result);
        assertEquals("quiz-123", result.getDocumentId());
        assertEquals("geografía", result.getTema());
        assertEquals(2, result.getSteps().size());
        verify(quizRepository, times(1)).findByDocumentId("quiz-123");
    }

    @Test
    void getDefinition_WhenQuizDoesNotExist_ShouldThrow() {
        // Arrange
        when(quizRepository.findByDocumentId("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> quizService.getDefinition("nonexistent"));
        verify(quizRepository, times(1)).findByDocumentId("nonexistent");
    }

    @Test
    void processResponse_ShouldCallInferenceClientAndReturnResult() {
        // Arrange
        String expectedResult = "Respuestas correctas: 2/2. Excelente trabajo en geografía.";
        when(inferenceClient.inferResult(any(QuizResponseRequest.class))).thenReturn(expectedResult);

        // Act
        String result = quizService.processResponse(testQuizResponse);

        // Assert
        assertEquals(expectedResult, result);
        verify(inferenceClient, times(1)).inferResult(testQuizResponse);
    }

    @Test
    void processResponse_WhenInferenceClientReturnsNull_ShouldReturnNull() {
        // Arrange
        when(inferenceClient.inferResult(any(QuizResponseRequest.class))).thenReturn(null);

        // Act
        String result = quizService.processResponse(testQuizResponse);

        // Assert
        assertNull(result);
        verify(inferenceClient, times(1)).inferResult(testQuizResponse);
    }

    @Test
    void listDocumentIds_ShouldReturnListOfIds() throws Exception {
        // Arrange
        QuizDefinitionEntity quiz1 = new QuizDefinitionEntity();
        quiz1.id = UUID.randomUUID();
        quiz1.documentId = "quiz-1";
        quiz1.stepsJson = objectMapper.writeValueAsString(Collections.emptyList());
        quiz1.createdAt = new Timestamp(System.currentTimeMillis());

        QuizDefinitionEntity quiz2 = new QuizDefinitionEntity();
        quiz2.id = UUID.randomUUID();
        quiz2.documentId = "quiz-2";
        quiz2.stepsJson = objectMapper.writeValueAsString(Collections.emptyList());
        quiz2.createdAt = new Timestamp(System.currentTimeMillis());

        QuizDefinitionEntity quiz3 = new QuizDefinitionEntity();
        quiz3.id = UUID.randomUUID();
        quiz3.documentId = "quiz-3";
        quiz3.stepsJson = objectMapper.writeValueAsString(Collections.emptyList());
        quiz3.createdAt = new Timestamp(System.currentTimeMillis());

        List<QuizDefinitionEntity> quizzes = Arrays.asList(quiz1, quiz2, quiz3);
        when(quizRepository.findAll()).thenReturn(quizzes);

        // Act
        List<String> result = quizService.listDocumentIds();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("quiz-1"));
        assertTrue(result.contains("quiz-2"));
        assertTrue(result.contains("quiz-3"));
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void listDocumentIds_WhenNoQuizzesExist_ShouldReturnEmptyList() {
        // Arrange
        when(quizRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<String> result = quizService.listDocumentIds();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void saveDefinition_WithNullDefinition_ShouldHandleGracefully() {
        // Act & Assert
        assertDoesNotThrow(() -> quizService.saveDefinition(null));
        verify(quizRepository, times(1)).save(null);
    }

    @Test
    void processResponse_WithCompleteRequest_ShouldPassAllFieldsToInferenceClient() {
        // Arrange
        String expectedResult = "Análisis completo realizado";
        when(inferenceClient.inferResult(any(QuizResponseRequest.class))).thenReturn(expectedResult);

        // Act
        String result = quizService.processResponse(testQuizResponse);

        // Assert
        assertEquals(expectedResult, result);

        // Verificar que se llamó con el objeto correcto
        verify(inferenceClient, times(1)).inferResult(argThat(request ->
                "quiz-123".equals(request.getDocumentId()) &&
                        "testUser".equals(request.getUsuario()) &&
                        "Evalúa mis respuestas".equals(request.getQuery()) &&
                        "conv-123".equals(request.getConversationId()) &&
                        "Proporciona feedback detallado".equals(request.getCustomPrompt()) &&
                        request.getRespuestas().size() == 2
        ));
    }

    @Test
    void processResponseWithLlm_ShouldCallLlmServiceWithCorrectContext() {
        // Arrange
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        String expectedLlmResponse = "Análisis: Respuesta correcta en geografía. Excelente conocimiento de capitales europeas y océanos.";
        when(llmService.generate(any(String.class), any(String.class))).thenReturn(expectedLlmResponse);

        // Act
        String result = quizService.processResponseWithLlm(testQuizResponse);

        // Assert
        assertEquals(expectedLlmResponse, result);
        verify(llmService, times(1)).generate(any(String.class), any(String.class));
        verify(quizRepository, times(1)).findByDocumentId("quiz-123");
    }

    @Test
    void processResponseWithLlm_WithCustomPrompt_ShouldUseQuizPrompt() {
        // Arrange
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        String customPrompt = "Analiza las respuestas en detalle y proporciona sugerencias específicas";
        testQuizResponse.setCustomPrompt(customPrompt);
        String expectedLlmResponse = "Análisis detallado con sugerencias específicas.";
        // The quiz has a prompt, so it should use that instead of the custom prompt
        when(llmService.generate(eq("Default prompt for testing"), any(String.class))).thenReturn(expectedLlmResponse);

        // Act
        String result = quizService.processResponseWithLlm(testQuizResponse);

        // Assert
        assertEquals(expectedLlmResponse, result);
        verify(llmService, times(1)).generate(eq("Default prompt for testing"), any(String.class));
    }

    @Test
    void processResponseWithLlm_WithNullCustomPrompt_ShouldUseQuizPrompt() {
        // Arrange
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        testQuizResponse.setCustomPrompt(null);
        String expectedLlmResponse = "Análisis general de las respuestas.";
        // The quiz has a prompt, so it should use that
        when(llmService.generate(eq("Default prompt for testing"), any(String.class))).thenReturn(expectedLlmResponse);

        // Act
        String result = quizService.processResponseWithLlm(testQuizResponse);

        // Assert
        assertEquals(expectedLlmResponse, result);
        verify(llmService, times(1)).generate(eq("Default prompt for testing"), any(String.class));
    }

    @Test
    void processResponseWithLlm_WithNoQuizPrompt_ShouldUseCustomPrompt() {
        // Arrange
        testQuizEntity.prompt = null; // No prompt in quiz
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        String customPrompt = "Analiza las respuestas en detalle y proporciona sugerencias específicas";
        testQuizResponse.setCustomPrompt(customPrompt);
        String expectedLlmResponse = "Análisis detallado con sugerencias específicas.";
        when(llmService.generate(eq(customPrompt), any(String.class))).thenReturn(expectedLlmResponse);

        // Act
        String result = quizService.processResponseWithLlm(testQuizResponse);

        // Assert
        assertEquals(expectedLlmResponse, result);
        verify(llmService, times(1)).generate(eq(customPrompt), any(String.class));
    }

    @Test
    void processResponseWithLlm_WithNoQuizPromptAndNoCustomPrompt_ShouldUseDefaultPrompt() {
        // Arrange
        testQuizEntity.prompt = null; // No prompt in quiz
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        testQuizResponse.setCustomPrompt(null);
        String expectedLlmResponse = "Análisis general de las respuestas.";
        String expectedDefaultPrompt = "Analiza las respuestas del usuario al quiz y proporciona feedback constructivo. " +
                "Evalúa la corrección de las respuestas y ofrece sugerencias de mejora cuando sea necesario.";
        when(llmService.generate(eq(expectedDefaultPrompt), any(String.class))).thenReturn(expectedLlmResponse);

        // Act
        String result = quizService.processResponseWithLlm(testQuizResponse);

        // Assert
        assertEquals(expectedLlmResponse, result);
        verify(llmService, times(1)).generate(eq(expectedDefaultPrompt), any(String.class));
    }

    @Test
    void processResponseWithLlm_WhenQuizNotFound_ShouldThrowException() {
        // Arrange
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> quizService.processResponseWithLlm(testQuizResponse));
        verify(llmService, never()).generate(any(String.class), any(String.class));
    }
}