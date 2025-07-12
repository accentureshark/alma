package org.shark.alma.infrastructure;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.shark.alma.llm.LlmService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LlmInferenceClientTest {

    @Mock
    private LlmService llmService;

    private LlmInferenceClient llmInferenceClient;

    @Test
    void generateResponse_ShouldCallLlmServiceWithPrompt() {
        // Arrange
        MockitoAnnotations.openMocks(this);
        llmInferenceClient = new LlmInferenceClient(llmService);
        
        String prompt = "Test prompt";
        String expectedResponse = "Test response";
        when(llmService.generate(prompt, "")).thenReturn(expectedResponse);

        // Act
        String result = llmInferenceClient.generateResponse(prompt);

        // Assert
        assertEquals(expectedResponse, result);
        verify(llmService, times(1)).generate(prompt, "");
    }

    @Test
    void generateResponse_WhenLlmServiceReturnsNull_ShouldReturnNull() {
        // Arrange
        MockitoAnnotations.openMocks(this);
        llmInferenceClient = new LlmInferenceClient(llmService);
        
        String prompt = "Test prompt";
        when(llmService.generate(prompt, "")).thenReturn(null);

        // Act
        String result = llmInferenceClient.generateResponse(prompt);

        // Assert
        assertNull(result);
        verify(llmService, times(1)).generate(prompt, "");
    }
}