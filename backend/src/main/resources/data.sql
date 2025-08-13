-- Insert test users (H2 doesn't support schema in the same way)
INSERT INTO "user" (id, email, password, administrador) VALUES 
('11111111-1111-1111-1111-111111111111', 'admin@alma.com', 'admin123', true);

INSERT INTO "user" (id, email, password, administrador) VALUES 
('22222222-2222-2222-2222-222222222222', 'user@alma.com', 'user123', false);

INSERT INTO "user" (id, email, password, administrador) VALUES 
('33333333-3333-3333-3333-333333333333', 'test@alma.com', 'test123', false);

-- Insert sample quiz definition (using H2 compatible JSON format)
INSERT INTO quiz.quiz_definition (
    id, document_id, tipo, tema, version, language, steps_json, prompt, created_at
) VALUES (
    '029856cb-8afa-4188-a6bd-117d0615c1c4',
    'orientacion_java_tech_vs_mgmt',
    'quiz',
    'orientacion_java_tech_vs_mgmt',
    '2.0',
    'es',
    '[{"step": 1, "id": "preferencia", "texto": "¿Qué disfrutas más en tu día a día de trabajo?", "opciones": ["Resolver problemas técnicos complejos", "Facilitar que el equipo alcance sus objetivos", "Pensar en arquitectura y decisiones a largo plazo", "Acompañar el crecimiento de otras personas"]}, {"step": 2, "id": "motivación", "texto": "¿Qué te motiva más?", "opciones": ["Aprender nuevas tecnologías", "Que el equipo funcione con eficiencia", "Resolver conflictos y remover impedimentos", "Ver el impacto del producto en el negocio"]}]',
    'Eres un asistente de orientación vocacional para programadores Java. Tu tarea es analizar las respuestas de un usuario a un cuestionario de preguntas y recomendar si el usuario se inclina más hacia el rol de **Tech Expert** o **Tech Manager**.',
    '2025-07-02 21:30:29.170983'
);