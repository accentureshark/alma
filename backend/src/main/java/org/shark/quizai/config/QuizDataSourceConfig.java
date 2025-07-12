package org.shark.quizai.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.shark.quizai.domain.port.out")
@EntityScan(basePackages = "org.shark.quizai.domain.model")
public class QuizDataSourceConfig {
    // Using default Spring Boot autoconfiguration for H2 database
    // Configuration is handled through application.yml
}