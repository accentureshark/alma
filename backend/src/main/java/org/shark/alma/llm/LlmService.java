package org.shark.alma.llm;

public interface LlmService {
    String generate(String prompt, String context);
}
