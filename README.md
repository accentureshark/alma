# Alma

**AI Learning & Measurement Assistant**

Alma es un proyecto que permite crear, almacenar y evaluar cualquier tipo de **quiz** de manera flexible. El backend escrito en Spring Boot gestiona las definiciones y respuestas de los cuestionarios en una base de datos PostgreSQL y, opcionalmente, interactúa con un modelo de lenguaje (LLM) para generar feedback automatizado.

## Características principales

- **Quizzes dinámicos**: las definiciones se almacenan en formato JSON, por lo que se pueden crear cuestionarios de cualquier tema sin modificar el código.
- **Persistencia**: el motor utiliza PostgreSQL (con scripts de inicialización en `backend/src/main/sql`) para guardar tanto las definiciones como las respuestas de los usuarios.
- **Interacción con LLM**: las respuestas pueden enviarse a un servicio de inferencia o a un modelo LLM local/externo mediante LangChain4j. Esto permite generar evaluaciones o explicaciones con IA.
- **Prompt adaptable**: cada petición puede incluir un prompt personalizado que el LLM usará al analizar las respuestas. Si no se indica, el sistema utiliza un prompt por defecto.
- **Arquitectura modular**: se sigue un estilo hexagonal que separa dominio, servicios y adaptadores. El frontend (React + Vite) se encuentra en la carpeta `ui`.
- **Perfiles diferenciados**: la aplicación web cuenta con un perfil de **Administrador** para gestionar cuestionarios y otro de **Usuario** para resolverlos.

## Estructura del repositorio

```
/backend - Código de la API en Spring Boot
/ui      - Aplicación web en React para responder o crear quizzes
```

El proyecto incluye un archivo `docker-compose.yml` que levanta tanto una instancia de PostgreSQL con los esquemas necesarios como un servicio de **Ollama**. Este último se utiliza para ejecutar localmente modelos de lenguaje que Alma emplea al generar feedback automatizado.

## Puesta en marcha rápida

1. **Servicios Docker**
   ```bash
   docker compose up -d alma-db ollama
   ```
   Esto crea la BD "alma" con los scripts iniciales y pone en marcha el servicio de Ollama.

2. **Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   El servidor quedará disponible en `http://localhost:8082`.

3. **Frontend**
   ```bash
   cd ui
   npm install
   npm run dev
   ```
   La interfaz se sirve por defecto en `http://localhost:5173`.

Con ambos servicios ejecutándose podrás cargar definiciones de quiz mediante `/api/quiz/upload`, responderlos y recibir feedback generado por IA a través de `/api/quiz/response/llm`.

## Licencia

Este proyecto se distribuye bajo la [Licencia Apache 2.0](LICENSE).
