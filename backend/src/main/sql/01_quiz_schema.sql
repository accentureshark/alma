-- backend/src/main/sql/01_quiz_schema.sql
CREATE SCHEMA IF NOT EXISTS quiz;

CREATE TABLE IF NOT EXISTS quiz.quiz_definition (
                                                    id UUID PRIMARY KEY,
                                                    document_id TEXT UNIQUE NOT NULL,
                                                    tipo TEXT,
                                                    tema TEXT,
                                                    version TEXT,
                                                    prompt TEXT,
                                                    steps_json JSONB NOT NULL,
                                                    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS quiz.quiz_response (
                                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                  quiz_document_id TEXT NOT NULL,
                                                  usuario TEXT NOT NULL,
                                                  respuestas JSONB NOT NULL,
                                                  resultado_inferencia TEXT,
                                                  calificacion DECIMAL(5,2),
                                                  created_at TIMESTAMP DEFAULT now()
);
CREATE TABLE IF NOT EXISTS quiz.users (
    id UUID PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    administrador BOOLEAN DEFAULT FALSE
);