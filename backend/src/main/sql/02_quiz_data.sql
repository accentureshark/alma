-- backend/src/main/sql/02_quiz_data.sql

INSERT INTO quiz.quiz_definition (
    id, document_id, tipo, tema, version, steps_json, prompt, created_at
) VALUES (
             '029856cb-8afa-4188-a6bd-117d0615c1c4',
             'orientacion_java_tech_vs_mgmt',
             'quiz',
             'orientacion_java_tech_vs_mgmt',
             '2.0',
             '[{"step": 1, "id": "preferencia", "texto": "¿Qué disfrutas más en tu día a día de trabajo?", "opciones": ["Resolver problemas técnicos complejos", "Facilitar que el equipo alcance sus objetivos", "Pensar en arquitectura y decisiones a largo plazo", "Acompañar el crecimiento de otras personas"]}, {"step": 2, "id": "motivación", "texto": "¿Qué te motiva más?", "opciones": ["Aprender nuevas tecnologías", "Que el equipo funcione con eficiencia", "Resolver conflictos y remover impedimentos", "Ver el impacto del producto en el negocio"]}, {"step": 3, "id": "toma_decisiones", "texto": "¿Te sentís cómodo tomando decisiones difíciles que afectan al equipo?", "opciones": ["Prefiero evitarlo, no es mi rol", "Si hay datos y contexto, sí", "No siempre, pero me esfuerzo en aprenderlo", "Sí, lo considero parte natural del liderazgo"]}, {"step": 4, "id": "liderazgo", "texto": "¿Cómo te sentís cuando tenés que liderar reuniones de equipo?", "opciones": ["Incómodo, prefiero que otros lo hagan", "Lo hago si es necesario, pero no me entusiasma", "Me gusta facilitar el diálogo y organizar ideas", "Disfruto ese tipo de situaciones, me sale natural"]}, {"step": 5, "id": "empatia", "texto": "¿Qué tan importante es para vos el bienestar del equipo?", "opciones": ["No es mi prioridad, eso lo maneja alguien más", "Importa, pero no más que entregar código", "Es clave para que el equipo funcione bien", "Es una de mis principales preocupaciones"]}, {"step": 6, "id": "aprendizaje", "texto": "¿Cómo manejás situaciones donde tenés que aprender una nueva tecnología?", "opciones": ["Investigo y pruebo por mi cuenta", "Pido ayuda a alguien que sepa más", "Armo un plan para que el equipo la entienda", "Organizo una capacitación o lo comparto en comunidad"]}, {"step": 7, "id": "delegación", "texto": "¿Qué tan cómodo te sentís delegando tareas técnicas a otros?", "opciones": ["Prefiero hacer todo yo para asegurar calidad", "Delego solo si confío mucho en la persona", "Me esfuerzo por delegar para balancear carga", "Confío y uso eso para empoderar al equipo"]}, {"step": 8, "id": "seguridad", "texto": "¿Qué tan seguro te sentís tomando decisiones técnicas importantes sin consultar a otros?", "opciones": ["Muy inseguro, siempre consulto", "Solo decido si estoy 100% seguro", "Prefiero consensuar antes de decidir", "Me siento capaz, pero valoro el input del equipo"]}, {"step": 9, "id": "resolución_problemas", "texto": "Cuando te enfrentás a un problema técnico, ¿qué preferís?", "opciones": ["Resolverlo solo, a fondo", "Proponer ideas y abrir la discusión", "Hacer pairing o mob programming", "Repartirlo en el equipo para resolver más rápido"]}, {"step": 10, "id": "relaciones_laborales", "texto": "¿Qué lugar ocupan para vos las conversaciones 1:1 con colegas o líderes?", "opciones": ["No me interesan mucho", "Me sirven si hay algo técnico para discutir", "Me ayudan a crecer profesionalmente", "Son esenciales para construir relaciones sanas"]}]',
             'Eres un asistente de orientación vocacional para programadores Java. Tu tarea es analizar las respuestas de un usuario a un cuestionario de preguntas y recomendar si el usuario se inclina más hacia el rol de **Tech Expert** o **Tech Manager**. Este análisis ayudará al usuario a entender mejor sus inclinaciones profesionales y a tomar decisiones informadas sobre su carrera.

         ### **Definiciones de Perfiles**
         - **Tech Expert**: Se enfoca en aspectos técnicos, como resolver problemas complejos, aprender y aplicar nuevas tecnologías, y tomar decisiones técnicas clave.
         - **Tech Manager**: Se concentra en la gestión de personas, facilitando el trabajo del equipo, removiendo obstáculos, y asegurando el bienestar y crecimiento de los miembros del equipo.

         ### **Instrucciones**
         1. El usuario proporcionará respuestas a las preguntas en orden numérico.
         2. Analiza el contenido de cada respuesta para determinar si se alinea más con el **Tech Expert** o el **Tech Manager**. Busca palabras clave, frases o temas que indiquen una preferencia por aspectos técnicos (por ejemplo, "resolver problemas técnicos", "aprender tecnologías") o por gestión de personas (por ejemplo, "liderar al equipo", "remover impedimentos").
         3. Si una respuesta es ambigua o no se alinea claramente con un perfil, considérala neutral o usa el contexto de las otras respuestas para interpretarla.
         4. Determina el perfil con el que más respuestas se alinean. Si las respuestas están equilibradas entre ambos perfiles, menciona los dos y explica por qué.
         5. Proporciona una recomendación clara y una justificación basada en el análisis de las respuestas del usuario.

         ### **Ejemplos de Análisis**
         - Si el usuario escribe "1. Me encanta depurar código y encontrar soluciones óptimas", esto se alinea con el **Tech Expert**.
         - Si el usuario escribe "2. Prefiero coordinar al equipo y asegurarme de que todos avancen", esto se alinea con el **Tech Manager**.
         - Si el usuario escribe "3. Disfruto tanto programar como guiar a otros", considera esto ambiguo y busca más contexto en las demás respuestas.

         ### **Formato de Salida**
         - **Recomendación**: [Indica si es Tech Expert, Tech Manager o un equilibrio entre ambos]
         - **Justificación**: [Explica cómo llegaste a esa conclusión basándote en las respuestas]',
             '2025-07-02T21:30:29.170983'
         )
    ON CONFLICT (document_id) DO NOTHING;