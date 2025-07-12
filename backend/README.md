# Alma AI Learning & Measurement Assistant

## 1. Propósito del Proyecto

Este proyecto, denominado "Alma AI Learning & Measurement Assistant", es una aplicación backend diseñada para gestionar y evaluar cuestionarios interactivos. Su característica principal es la capacidad de procesar las respuestas de los usuarios y utilizar un motor de inferencia externo para obtener resultados o conclusiones basadas en dichas respuestas.

La aplicación permite definir cuestionarios dinámicos en formato JSON, almacenarlos, y luego servirlos a los usuarios. Una vez que un usuario completa un cuestionario, sus respuestas son enviadas a un servicio de inferencia que devuelve un resultado.

## 2. Arquitectura

El proyecto sigue una **Arquitectura Hexagonal** (también conocida como Puertos y Adaptadores), lo que le permite desacoplar el núcleo de la lógica de negocio de los detalles de la infraestructura (como la base de datos, APIs externas o la interfaz de usuario).

La estructura se organiza de la siguiente manera:

*   **Dominio (`domain`):** Contiene la lógica de negocio y las entidades principales.
    *   `model`: Define los objetos del dominio como `QuizDefinition` (la estructura de un cuestionario) y `QuizResponseRequest` (las respuestas de un usuario).
    *   `port/out`: Define las interfaces (puertos) que necesita el dominio para comunicarse con el exterior, como `QuizRepository` (para persistencia) e `InferenceClient` (para la inferencia).

*   **Aplicación (`application`):** Orquesta el flujo de datos y contiene los casos de uso.
    *   `service/QuizService`: Es el corazón de la aplicación. Utiliza los puertos de salida para implementar la lógica de negocio, como guardar una definición de quiz o procesar una respuesta.

*   **Adaptadores (`adapter`):** Son las implementaciones concretas de los puertos.
    *   `in/rest/QuizController`: Es un **adaptador de entrada** que expone la funcionalidad de la aplicación a través de una API REST. Recibe peticiones HTTP y llama al `QuizService`.
    *   `infrastructure`: Contiene los **adaptadores de salida**. Aunque no se muestra la implementación de `QuizRepository`, se infiere que interactúa con una base de datos. `InferenceClientImpl` sería la implementación que llama al servicio de IA.

## 3. Archivos Importantes

*   `pom.xml`: Define las dependencias del proyecto, como Spring Boot (para el servidor web), Spring Data JPA (para la base de datos), PostgreSQL (como motor de base de datos), y `springdoc-openapi` (para la documentación de la API con Swagger). También incluye una dependencia al módulo `rag`, lo que sugiere que el motor de inferencia podría estar relacionado con un sistema de Retrieval-Augmented Generation.

*   `application.yml`: Archivo de configuración de Spring Boot. Define el puerto del servidor (8082), la configuración de la conexión a la base de datos PostgreSQL, y los niveles de logging para depuración.

*   `QuizController.java`: El punto de entrada de la API REST. Define los siguientes endpoints:
    *   `POST /api/quiz/upload`: Para subir y guardar la definición de un nuevo quiz.
    *   `GET /api/quiz/{documentId}`: Para obtener la definición de un quiz existente.
    *   `POST /api/quiz/response`: Para enviar las respuestas de un usuario y obtener un resultado del motor de inferencia.
    *   `GET /api/quiz/list`: Para listar todos los quizzes disponibles.
    *   `GET /api/quiz/step`: Para obtener un paso específico de un quiz.

*   `QuizService.java`: Contiene la lógica de negocio principal. Coordina las operaciones entre la API REST y los sistemas externos (base de datos y motor de inferencia).

*   `InferenceClient.java`: Interfaz que define el contrato para comunicarse con el motor de inferencia. Esto permite cambiar la implementación del motor de inferencia sin afectar al resto de la aplicación.

*   `quiz_schema.sql`: Define el esquema de la base de datos. Contiene dos tablas principales:
    *   `quiz.quiz_definition`: Almacena las definiciones de los quizzes, incluyendo los pasos en formato JSON.
    *   `quiz.quiz_response`: Almacena las respuestas de los usuarios a un quiz, junto con el resultado de la inferencia.

## 4. Funcionalidad Principal

1.  **Definición de Quizzes**: Un administrador o sistema externo puede subir una definición de quiz en formato JSON a través del endpoint `/api/quiz/upload`. Esta definición se guarda en la base de datos.
2.  **Obtención de Quizzes**: Los usuarios pueden consultar la definición de un quiz específico mediante `/api/quiz/{documentId}` o listar todos los quizzes disponibles con `/api/quiz/list`.
3.  **Resolución y Evaluación**: Los usuarios envían sus respuestas a través de `/api/quiz/response`. El backend almacena las respuestas y consulta un motor de inferencia externo (por ejemplo, un sistema RAG) para obtener un resultado personalizado.
4.  **Navegación por Pasos**: El endpoint `/api/quiz/step` permite obtener información de un paso específico de un quiz, facilitando la navegación paso a paso.

## 5. Novedades y Mejoras Recientes

- **Persistencia Mejorada**: Ahora se almacenan tanto las definiciones como las respuestas de los quizzes, junto con los resultados de inferencia, en la base de datos PostgreSQL.
- **DTOs para Integración con IA**: Se agregaron DTOs (`RagQueryRequest`, `QueryResponse`) para estructurar la comunicación con el motor de inferencia.
- **Configuración Modular**: El backend cuenta con clases de configuración dedicadas para la base de datos y Swagger/OpenAPI.
- **Soporte para múltiples quizzes y pasos**: Se mejoró la estructura para soportar múltiples quizzes y navegación granular por pasos.
- **Documentación OpenAPI**: El backend expone la documentación de la API de forma automática usando `springdoc-openapi`.

## 6. Estructura de Carpetas

- `domain/model`: Entidades y modelos de dominio (`QuizDefinition`, `QuizResponseRequest`, etc.).
- `domain/port`: Interfaces de puertos de entrada y salida.
- `application/service`: Lógica de negocio y casos de uso (`QuizService`).
- `adapter/in/rest`: Controladores REST (`QuizController`).
- `infrastructure`: Implementaciones concretas de puertos, integración con IA y DTOs.
- `config`: Configuración de la base de datos y Swagger.
- `resources/sql`: Scripts SQL para el esquema y datos iniciales.

## 7. Ejecución y Configuración

- El backend se ejecuta en el puerto `8082` por defecto.
- La configuración de la base de datos y otros parámetros se encuentran en `src/main/resources/application.yml`.
- Para inicializar la base de datos, se incluyen los scripts `quiz_schema.sql` y `quiz_data.sql`.

## 8. Dependencias Clave

- Spring Boot, Spring Data JPA, PostgreSQL, springdoc-openapi, y un módulo RAG para inferencia.
