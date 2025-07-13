package org.shark.alma.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDefinition {
    private String documentId;
    private String tipo;
    private String tema;
    private String version;
    private String prompt;
    private List<QuizStep> steps;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizStep {
        private int step;
        private String id;
        private String texto;
        private List<String> opciones;
    }
}