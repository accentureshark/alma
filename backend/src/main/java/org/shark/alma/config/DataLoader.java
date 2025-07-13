package org.shark.alma.config;

import org.shark.alma.domain.model.QuizDefinition;
import org.shark.alma.application.service.QuizService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final QuizService quizService;

    public DataLoader(QuizService quizService) {
        this.quizService = quizService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Load sample quiz data
        loadSampleQuizzes();
    }

    private void loadSampleQuizzes() {
        // Sample quiz 1
        QuizDefinition quiz1 = new QuizDefinition();
        quiz1.setDocumentId("quiz-orientacion-vocacional");
        quiz1.setTema("Orientación Vocacional Java Tech vs Management");
        quiz1.setTipo("quiz");
        quiz1.setVersion("2.0");
        quiz1.setPrompt("Analiza las respuestas del usuario para determinar su orientación vocacional entre roles técnicos y de gestión en el desarrollo de software Java.");
        
        QuizDefinition.QuizStep step1 = new QuizDefinition.QuizStep();
        step1.setStep(1);
        step1.setId("preferencia");
        step1.setTexto("¿Qué disfrutas más en tu día a día de trabajo?");
        step1.setOpciones(List.of(
            "Resolver problemas técnicos complejos",
            "Facilitar que el equipo alcance sus objetivos",
            "Pensar en arquitectura y decisiones a largo plazo",
            "Acompañar el crecimiento de otras personas"
        ));
        
        QuizDefinition.QuizStep step2 = new QuizDefinition.QuizStep();
        step2.setStep(2);
        step2.setId("motivacion");
        step2.setTexto("¿Qué te motiva más?");
        step2.setOpciones(List.of(
            "Aprender nuevas tecnologías",
            "Que el equipo funcione con eficiencia",
            "Resolver conflictos y remover impedimentos",
            "Ver el impacto del producto en el negocio"
        ));
        
        QuizDefinition.QuizStep step3 = new QuizDefinition.QuizStep();
        step3.setStep(3);
        step3.setId("liderazgo");
        step3.setTexto("¿Cómo te sentís cuando tenés que liderar reuniones de equipo?");
        step3.setOpciones(List.of(
            "Incómodo, prefiero que otros lo hagan",
            "Lo hago si es necesario, pero no me entusiasma",
            "Me gusta facilitar el diálogo y organizar ideas",
            "Disfruto ese tipo de situaciones, me sale natural"
        ));
        
        quiz1.setSteps(List.of(step1, step2, step3));
        
        // Sample quiz 2
        QuizDefinition quiz2 = new QuizDefinition();
        quiz2.setDocumentId("quiz-onboarding");
        quiz2.setTema("Onboarding de Nuevos Talentos");
        quiz2.setTipo("quiz");
        quiz2.setVersion("1.0");
        quiz2.setPrompt("Evalúa el proceso de onboarding y proporciona recomendaciones para mejorar la experiencia de nuevos talentos.");
        
        QuizDefinition.QuizStep step2_1 = new QuizDefinition.QuizStep();
        step2_1.setStep(1);
        step2_1.setId("experiencia");
        step2_1.setTexto("¿Cómo calificarías tu experiencia general de onboarding?");
        step2_1.setOpciones(List.of(
            "Excelente",
            "Buena",
            "Regular",
            "Mala"
        ));
        
        QuizDefinition.QuizStep step2_2 = new QuizDefinition.QuizStep();
        step2_2.setStep(2);
        step2_2.setId("documentacion");
        step2_2.setTexto("¿La documentación provista fue clara y útil?");
        step2_2.setOpciones(List.of(
            "Muy clara y completa",
            "Mayormente clara",
            "Algo confusa",
            "Muy confusa o incompleta"
        ));
        
        quiz2.setSteps(List.of(step2_1, step2_2));
        
        // Save the sample quizzes
        quizService.saveDefinition(quiz1);
        quizService.saveDefinition(quiz2);
        
        System.out.println("Sample quizzes loaded successfully");
    }
}