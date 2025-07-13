
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
      <ul>
        {quiz.steps.map((step) => (
          <li key={step.id}>{step.texto}</li>
        ))}
      </ul>
    </div>
  );
};
