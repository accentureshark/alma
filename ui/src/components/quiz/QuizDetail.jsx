
export const QuizDetail = ({ quiz }) => {
  if (!quiz) {
    return (
      <div className="quiz-detail-container quiz-detail-placeholder">
        <p>Selecciona un quiz para ver sus detalles.</p>
      </div>
    );
  }

  return (
    <div className="quiz-detail-container">
      <h3>{quiz.tema}</h3>
      
      {quiz.prompt && (
        <div className="quiz-prompt-section">
          <h4>Prompt LLM:</h4>
          <p className="quiz-prompt">{quiz.prompt}</p>
        </div>
      )}

      <h4>Preguntas:</h4>
      {quiz.steps.map((step) => (
        <div key={step.id} className="quiz-step">
          <h5>Pregunta {step.step}: {step.texto}</h5>
          {step.opciones && step.opciones.length > 0 && (
            <div className="quiz-options">
              <h6>Opciones:</h6>
              <ul>
                {step.opciones.map((opcion, index) => (
                  <li key={index}>{opcion}</li>
                ))}
              </ul>
            </div>
          )}
        </div>
      ))}
    </div>
  );
};
