package org.shark.alma.llm;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class LlmFactory {
    public static ChatLanguageModel createChatModel(String provider, String model, String baseUrl, String apiKey) {
        switch (provider.toLowerCase()) {
            case "openai":
                return OpenAiChatModel.builder()
                        .apiKey(apiKey)
                        .modelName(model)
                        .baseUrl(baseUrl)
                        .build();
            case "ollama":
                return OllamaChatModel.builder()
                        .baseUrl(baseUrl)
                        .modelName(model)
                        .build();
            // Puedes agregar más proveedores aquí.
            default:
                throw new IllegalArgumentException("Proveedor LLM no soportado: " + provider);
        }
    }
}
