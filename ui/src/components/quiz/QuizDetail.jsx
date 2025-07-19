
import { useAuth } from '../../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { CustomButton } from '../ui/CustomButton';

export const QuizDetail = ({ quiz, onEdit }) => {
  const { user } = useAuth();
  const navigate = useNavigate();

  if (!quiz) {
    return (
      <div className="quiz-detail-container quiz-detail-placeholder">
        <p>Selecciona un quiz para ver sus detalles.</p>
      </div>
    );
  }

  const handleTestQuiz = () => {
    navigate(`/quiz/${quiz.documentId}`);
  };

  return (
    <div className="quiz-detail-container">
      <div className="quiz-detail-header">
        <h3>{quiz.tema}</h3>
        <div className="quiz-detail-actions">
          {onEdit && (
            <CustomButton 
              label="Editar"
              icon="pi pi-pencil"
              onClick={() => onEdit(quiz)}
              severity="secondary"
              size="small"
            />
          )}
          {user && user.isAdmin && (
            <CustomButton 
              label="Probar Quiz"
              icon="pi pi-play"
              onClick={handleTestQuiz}
              severity="success"
              size="small"
            />
          )}
        </div>
      </div>
      
      {quiz.prompt && (
        <div className="quiz-prompt-section">
          <h4>Prompt LLM:</h4>
          <p className="quiz-prompt">{quiz.prompt}</p>
        </div>
      )}

      <h4>Preguntas:</h4>
      {quiz.steps.map((step) => (
        <div key={step.id} className="quiz-step">
          <h5>
            Pregunta {step.step}: {step.texto}
            {step.random && (
              <span className="random-indicator" title="Opciones en orden aleatorio">
                <i className="pi pi-refresh" style={{ marginLeft: '0.5rem', color: '#10b981' }}></i>
              </span>
            )}
          </h5>
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
