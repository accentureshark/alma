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
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private InferenceClient inferenceClient;

    @Mock
    private LlmService llmService;

    @Mock
    private QuizResultService quizResultService;

    private QuizService quizService;

    private QuizDefinition testQuizDefinition;
    private QuizDefinitionEntity testQuizEntity;
    private QuizResponseRequest testQuizResponse;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        // Manually create the QuizService with mocked dependencies
        quizService = new QuizService(quizRepository, inferenceClient, llmService, quizResultService);
        
        // Mock QuizResultService methods with lenient stubbing since they're not used in all tests
        lenient().when(quizResultService.calculateScore(any(String.class))).thenReturn(java.math.BigDecimal.valueOf(8.5));
        lenient().when(quizResultService.saveQuizResult(any(String.class), any(String.class), any(Map.class), any(String.class), any(java.math.BigDecimal.class)))
                .thenReturn(null); // We don't need the returned QuizResult for these tests
        
        QuizDefinition.QuizStep step1 = new QuizDefinition.QuizStep(
                1, "step1", "¿Cuál es la capital de Francia?",
                Arrays.asList("Madrid", "París", "Londres", "Roma"), false
        );
        QuizDefinition.QuizStep step2 = new QuizDefinition.QuizStep(
                2, "step2", "¿Cuál es el océano más grande?",
                Arrays.asList("Atlántico", "Índico", "Pacífico", "Ártico"), false
        );

        testQuizDefinition = new QuizDefinition(
                "quiz-123", "educativo", "geografía", "1.0", "Default prompt for testing",
                Arrays.asList(step1, step2)
        );

        testQuizEntity = new QuizDefinitionEntity();
        testQuizEntity.setId(UUID.randomUUID());
        testQuizEntity.setDocumentId("quiz-123");
        testQuizEntity.setTipo("educativo");
        testQuizEntity.setTema("geografía");
        testQuizEntity.setVersion("1.0");
        testQuizEntity.setPrompt("Default prompt for testing");
        testQuizEntity.setStepsJson(objectMapper.writeValueAsString(Arrays.asList(step1, step2)));
        testQuizEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));

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
        quizService.saveDefinition(testQuizDefinition);
        verify(quizRepository, times(1)).save(any(QuizDefinitionEntity.class));
    }

    @Test
    void getDefinition_WhenQuizExists_ShouldReturnQuiz() {
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        QuizDefinition result = quizService.getDefinition("quiz-123");
        assertNotNull(result);
        assertEquals("quiz-123", result.getDocumentId());
        assertEquals("geografía", result.getTema());
        assertEquals(2, result.getSteps().size());
        verify(quizRepository, times(1)).findByDocumentId("quiz-123");
    }

    @Test
    void getDefinition_WhenQuizDoesNotExist_ShouldThrow() {
        when(quizRepository.findByDocumentId("nonexistent")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> quizService.getDefinition("nonexistent"));
        verify(quizRepository, times(1)).findByDocumentId("nonexistent");
    }

    @Test
    void processResponse_ShouldCallInferenceClientAndReturnResult() {
        String expectedResult = "Respuestas correctas: 2/2. Excelente trabajo en geografía.";
        when(inferenceClient.inferResult(any(QuizResponseRequest.class))).thenReturn(expectedResult);
        String result = quizService.processResponse(testQuizResponse);
        assertEquals(expectedResult, result);
        verify(inferenceClient, times(1)).inferResult(testQuizResponse);
    }

    @Test
    void processResponse_WhenInferenceClientReturnsNull_ShouldReturnNull() {
        when(inferenceClient.inferResult(any(QuizResponseRequest.class))).thenReturn(null);
        String result = quizService.processResponse(testQuizResponse);
        assertNull(result);
        verify(inferenceClient, times(1)).inferResult(testQuizResponse);
    }

    @Test
    void listDocumentIds_ShouldReturnListOfIds() throws Exception {
        QuizDefinitionEntity quiz1 = new QuizDefinitionEntity();
        quiz1.setId(UUID.randomUUID());
        quiz1.setDocumentId("quiz-1");
        quiz1.setStepsJson(objectMapper.writeValueAsString(Collections.emptyList()));
        quiz1.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        QuizDefinitionEntity quiz2 = new QuizDefinitionEntity();
        quiz2.setId(UUID.randomUUID());
        quiz2.setDocumentId("quiz-2");
        quiz2.setStepsJson(objectMapper.writeValueAsString(Collections.emptyList()));
        quiz2.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        QuizDefinitionEntity quiz3 = new QuizDefinitionEntity();
        quiz3.setId(UUID.randomUUID());
        quiz3.setDocumentId("quiz-3");
        quiz3.setStepsJson(objectMapper.writeValueAsString(Collections.emptyList()));
        quiz3.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        List<QuizDefinitionEntity> quizzes = Arrays.asList(quiz1, quiz2, quiz3);
        when(quizRepository.findAll()).thenReturn(quizzes);

        List<String> result = quizService.listDocumentIds();
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("quiz-1"));
        assertTrue(result.contains("quiz-2"));
        assertTrue(result.contains("quiz-3"));
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void listDocumentIds_WhenNoQuizzesExist_ShouldReturnEmptyList() {
        when(quizRepository.findAll()).thenReturn(Collections.emptyList());
        List<String> result = quizService.listDocumentIds();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void saveDefinition_WithNullDefinition_ShouldHandleGracefully() {
        assertDoesNotThrow(() -> quizService.saveDefinition(null));
        verify(quizRepository, times(1)).save(null);
    }

    @Test
    void processResponse_WithCompleteRequest_ShouldPassAllFieldsToInferenceClient() {
        String expectedResult = "Análisis completo realizado";
        when(inferenceClient.inferResult(any(QuizResponseRequest.class))).thenReturn(expectedResult);
        String result = quizService.processResponse(testQuizResponse);
        assertEquals(expectedResult, result);
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
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        String expectedLlmResponse = "Análisis: Respuesta correcta en geografía. Excelente conocimiento de capitales europeas y océanos.";
        when(llmService.generate(any(String.class), any(String.class))).thenReturn(expectedLlmResponse);
        String result = quizService.processResponseWithLlm(testQuizResponse);
        assertEquals(expectedLlmResponse, result);
        verify(llmService, times(1)).generate(any(String.class), any(String.class));
        verify(quizRepository, times(1)).findByDocumentId("quiz-123");
    }

    @Test
    void processResponseWithLlm_WithCustomPrompt_ShouldUseQuizPrompt() {
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        String customPrompt = "Analiza las respuestas en detalle y proporciona sugerencias específicas";
        testQuizResponse.setCustomPrompt(customPrompt);
        String expectedLlmResponse = "Análisis detallado con sugerencias específicas.";
        when(llmService.generate(eq("Default prompt for testing"), any(String.class))).thenReturn(expectedLlmResponse);
        String result = quizService.processResponseWithLlm(testQuizResponse);
        assertEquals(expectedLlmResponse, result);
        verify(llmService, times(1)).generate(eq("Default prompt for testing"), any(String.class));
    }

    @Test
    void processResponseWithLlm_WithNullCustomPrompt_ShouldUseQuizPrompt() {
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        testQuizResponse.setCustomPrompt(null);
        String expectedLlmResponse = "Análisis general de las respuestas.";
        when(llmService.generate(eq("Default prompt for testing"), any(String.class))).thenReturn(expectedLlmResponse);
        String result = quizService.processResponseWithLlm(testQuizResponse);
        assertEquals(expectedLlmResponse, result);
        verify(llmService, times(1)).generate(eq("Default prompt for testing"), any(String.class));
    }

    @Test
    void processResponseWithLlm_WithNoQuizPrompt_ShouldUseCustomPrompt() {
        testQuizEntity.setPrompt(null);
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        String customPrompt = "Analiza las respuestas en detalle y proporciona sugerencias específicas";
        testQuizResponse.setCustomPrompt(customPrompt);
        String expectedLlmResponse = "Análisis detallado con sugerencias específicas.";
        when(llmService.generate(eq(customPrompt), any(String.class))).thenReturn(expectedLlmResponse);
        String result = quizService.processResponseWithLlm(testQuizResponse);
        assertEquals(expectedLlmResponse, result);
        verify(llmService, times(1)).generate(eq(customPrompt), any(String.class));
    }

    @Test
    void processResponseWithLlm_WithNoQuizPromptAndNoCustomPrompt_ShouldUseDefaultPrompt() {
        testQuizEntity.setPrompt(null);
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.of(testQuizEntity));
        testQuizResponse.setCustomPrompt(null);
        String expectedLlmResponse = "Análisis general de las respuestas.";
        String expectedDefaultPrompt = "Analiza las respuestas del usuario al quiz y proporciona feedback constructivo. " +
                "Evalúa la corrección de las respuestas y ofrece sugerencias de mejora cuando sea necesario.";
        when(llmService.generate(eq(expectedDefaultPrompt), any(String.class))).thenReturn(expectedLlmResponse);
        String result = quizService.processResponseWithLlm(testQuizResponse);
        assertEquals(expectedLlmResponse, result);
        verify(llmService, times(1)).generate(eq(expectedDefaultPrompt), any(String.class));
    }

    @Test
    void processResponseWithLlm_WhenQuizNotFound_ShouldThrowException() {
        when(quizRepository.findByDocumentId("quiz-123")).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> quizService.processResponseWithLlm(testQuizResponse));
        verify(llmService, never()).generate(any(String.class), any(String.class));
    }

    @Test
    void getDefinition_WithRandomFlag_ShouldRandomizeOptions() throws Exception {
        // Arrange: Create steps with random flag enabled
        QuizDefinition.QuizStep stepWithRandom = new QuizDefinition.QuizStep(
                1, "step1", "¿Cuál es la capital de Francia?",
                Arrays.asList("Madrid", "París", "Londres", "Roma"), true
        );
        QuizDefinition.QuizStep stepWithoutRandom = new QuizDefinition.QuizStep(
                2, "step2", "¿Cuál es el océano más grande?",
                Arrays.asList("Atlántico", "Índico", "Pacífico", "Ártico"), false
        );

        QuizDefinitionEntity entityWithRandom = new QuizDefinitionEntity();
        entityWithRandom.setId(UUID.randomUUID());
        entityWithRandom.setDocumentId("quiz-random");
        entityWithRandom.setTipo("educativo");
        entityWithRandom.setTema("geografía");
        entityWithRandom.setVersion("1.0");
        entityWithRandom.setPrompt("Test prompt");
        entityWithRandom.setStepsJson(objectMapper.writeValueAsString(Arrays.asList(stepWithRandom, stepWithoutRandom)));
        entityWithRandom.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        when(quizRepository.findByDocumentId("quiz-random")).thenReturn(Optional.of(entityWithRandom));

        // Act: Call multiple times to verify randomization (options will be different sometimes)
        List<String> originalOptions = Arrays.asList("Madrid", "París", "Londres", "Roma");
        List<String> constantOptions = Arrays.asList("Atlántico", "Índico", "Pacífico", "Ártico");
        
        QuizDefinition result1 = quizService.getDefinition("quiz-random");
        QuizDefinition result2 = quizService.getDefinition("quiz-random");

        // Assert: Random step should have possibly different order, non-random should be same
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(2, result1.getSteps().size());
        assertEquals(2, result2.getSteps().size());

        // Step 1 has random=true, so options may be in different order
        QuizDefinition.QuizStep randomStep1 = result1.getSteps().get(0);
        QuizDefinition.QuizStep randomStep2 = result2.getSteps().get(0);
        assertTrue(randomStep1.isRandom());
        assertTrue(randomStep2.isRandom());
        assertEquals(originalOptions.size(), randomStep1.getOpciones().size());
        assertEquals(originalOptions.size(), randomStep2.getOpciones().size());
        assertTrue(randomStep1.getOpciones().containsAll(originalOptions));
        assertTrue(randomStep2.getOpciones().containsAll(originalOptions));

        // Step 2 has random=false, so options should remain in original order
        QuizDefinition.QuizStep constantStep1 = result1.getSteps().get(1);
        QuizDefinition.QuizStep constantStep2 = result2.getSteps().get(1);
        assertFalse(constantStep1.isRandom());
        assertFalse(constantStep2.isRandom());
        assertEquals(constantOptions, constantStep1.getOpciones());
        assertEquals(constantOptions, constantStep2.getOpciones());
    }

    @Test
    void getDefinition_WithRandomFlagButEmptyOptions_ShouldNotFail() throws Exception {
        // Arrange: Create step with random flag but empty options
        QuizDefinition.QuizStep stepWithRandomEmptyOptions = new QuizDefinition.QuizStep(
                1, "step1", "¿Cuál es la capital de Francia?",
                Collections.emptyList(), true
        );

        QuizDefinitionEntity entityWithRandomEmpty = new QuizDefinitionEntity();
        entityWithRandomEmpty.setId(UUID.randomUUID());
        entityWithRandomEmpty.setDocumentId("quiz-random-empty");
        entityWithRandomEmpty.setTipo("educativo");
        entityWithRandomEmpty.setTema("geografía");
        entityWithRandomEmpty.setVersion("1.0");
        entityWithRandomEmpty.setPrompt("Test prompt");
        entityWithRandomEmpty.setStepsJson(objectMapper.writeValueAsString(Arrays.asList(stepWithRandomEmptyOptions)));
        entityWithRandomEmpty.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        when(quizRepository.findByDocumentId("quiz-random-empty")).thenReturn(Optional.of(entityWithRandomEmpty));

        // Act & Assert: Should not fail and return empty options
        QuizDefinition result = quizService.getDefinition("quiz-random-empty");
        assertNotNull(result);
        assertEquals(1, result.getSteps().size());
        assertTrue(result.getSteps().get(0).isRandom());
        assertTrue(result.getSteps().get(0).getOpciones().isEmpty());
    }
}