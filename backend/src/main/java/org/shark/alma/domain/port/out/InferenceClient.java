package org.shark.alma.domain.port.out;



import org.shark.alma.domain.model.QuizResponseRequest;

public interface InferenceClient {
    String inferResult(QuizResponseRequest request);
}

