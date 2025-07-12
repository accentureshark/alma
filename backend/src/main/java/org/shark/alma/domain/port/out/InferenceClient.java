package org.shark.alma.domain.port.out;

public interface InferenceClient {
    String generateResponse(String prompt);
}

