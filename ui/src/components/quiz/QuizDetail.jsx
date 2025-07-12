
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
      <ul>
        {quiz.steps.map((step) => (
          <li key={step.id}>{step.texto}</li>
        ))}
      </ul>
    </div>
  );
};
