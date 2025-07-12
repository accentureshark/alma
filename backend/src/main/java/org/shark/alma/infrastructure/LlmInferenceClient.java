package org.shark.alma.infrastructure;

import org.shark.alma.domain.port.out.InferenceClient;
import org.shark.alma.llm.LlmService;
import org.springframework.stereotype.Component;

@Component
public class LlmInferenceClient implements InferenceClient {

    private final LlmService llmService;

    public LlmInferenceClient(LlmService llmService) {
        this.llmService = llmService;
    }

    @Override
    public String generateResponse(String prompt) {
        // Use empty context for the LLM service
        return llmService.generate(prompt, "");
    }
}