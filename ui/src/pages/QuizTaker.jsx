import '../styles/quiz-taker.css'
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Dialog } from 'primereact/dialog';
import { InputTextarea } from 'primereact/inputtextarea';
import { Divider } from 'primereact/divider';
import { Header } from '../components/layout/Header';
import { CustomButton } from '../components/ui/CustomButton';
import { TextareaField } from '../components/ui/TextareaField';
import { CustomCard } from '../components/ui/CustomCard';

// Datos de ejemplo (pueden eliminarse si solo usas los del backend)
const exampleQuizzes = [
  {
    documentId: '1',
    tema: 'Quiz de Bienvenida',
    steps: [
      { step: 1, id: '1', texto: '¿Cuál es tu rol en la empresa?' },
      { step: 2, id: '2', texto: '¿Qué esperas aprender en tu primer mes?' },
      { step: 3, id: '3', texto: '¿Cuáles son tus metas a largo plazo?' },
    ],
  },
  {
    documentId: '2',
    tema: 'Evaluación de Desempeño',
    steps: [
      { step: 1, id: '1', texto: 'Pregunta A' },
      { step: 2, id: '2', texto: 'Pregunta B' },
    ],
  },
];

const QuizTaker = () => {
  const { quizId } = useParams();
  const navigate = useNavigate();
  const [quiz, setQuiz] = useState(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [answers, setAnswers] = useState({});
  const [isTransitioning, setIsTransitioning] = useState(false);
  const [showCompletionModal, setShowCompletionModal] = useState(false);
  const [allQuestionsAnswered, setAllQuestionsAnswered] = useState(false);
  const [customPrompt, setCustomPrompt] = useState('');
  const [llmResponse, setLlmResponse] = useState('');
  const [isProcessingLlm, setIsProcessingLlm] = useState(false);
  const [showLlmResult, setShowLlmResult] = useState(false);

  useEffect(() => {
    // Intenta cargar el quiz desde el backend
    const fetchQuiz = async () => {
      try {
        const baseUrl = import.meta.env.VITE_API_URL || '/api';
        const res = await fetch(`${baseUrl}/quiz/${quizId}`);
        if (res.ok) {
          const data = await res.json();
          setQuiz(data);
        } else {
          // Fallback a quizzes de ejemplo si no se encuentra en backend
          const foundQuiz = exampleQuizzes.find((q) => q.documentId === quizId);
          setQuiz(foundQuiz);
        }
      } catch (error) {
        // Fallback a quizzes de ejemplo en caso de error
        const foundQuiz = exampleQuizzes.find((q) => q.documentId === quizId);
        setQuiz(foundQuiz);
      }
    };
    fetchQuiz();
  }, [quizId]);

  useEffect(() => {
    if (!quiz) return;
    // Check if all questions were answered
    const allAnswered = quiz.steps.every(q => {
      if (Array.isArray(q.opciones) && q.opciones.length > 0) {
        return answers[q.id] && answers[q.id] !== '';
      } else {
        return answers[q.id] && answers[q.id].trim() !== '';
      }
    });
    setAllQuestionsAnswered(allAnswered);
  }, [answers, quiz]);

  const handleAnswerChange = (e) => {
    // Para radio: e.target.value, para textarea: e.target.value
    const value = e.target.value;
    const qid = quiz.steps[currentQuestionIndex].id;
    setAnswers(prev => ({ ...prev, [qid]: value }));
  };

  const handleQuestionTransition = (nextIdx) => {
    setIsTransitioning(true);
    setTimeout(() => {
      setCurrentQuestionIndex(nextIdx);
      setIsTransitioning(false);
    }, 120);
  };

  const goToNextQuestion = () => {
    if (currentQuestionIndex < quiz.steps.length - 1) {
      handleQuestionTransition(currentQuestionIndex + 1);
    }
  };

  const goToPreviousQuestion = () => {
    if (currentQuestionIndex > 0) {
      handleQuestionTransition(currentQuestionIndex - 1);
    }
  };

  const handleSubmitQuiz = async () => {
    setIsProcessingLlm(true);
    try {
      const baseUrl = import.meta.env.VITE_API_URL || '/api';
      const requestData = {
        documentId: quizId,
        usuario: 'test-user', // En un sistema real, esto vendría de la autenticación
        respuestas: answers,
        customPrompt: customPrompt || 'Analiza las respuestas del usuario al quiz y proporciona feedback constructivo. Evalúa la corrección de las respuestas y ofrece sugerencias de mejora cuando sea necesario.'
      };

      const response = await fetch(`${baseUrl}/quiz/response/llm`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestData),
      });

      if (response.ok) {
        const result = await response.text();
        setLlmResponse(result);
        setShowLlmResult(true);
      } else {
        throw new Error('Error al procesar la respuesta');
      }
    } catch (error) {
      console.error('Error:', error);
      setLlmResponse('Error al procesar las respuestas. Por favor, intenta nuevamente.');
      setShowLlmResult(true);
    } finally {
      setIsProcessingLlm(false);
    }
    setShowCompletionModal(true);
  };

  const handleCloseModal = () => {
    setShowCompletionModal(false);
    setShowLlmResult(false);
    setLlmResponse('');
    setCustomPrompt('');
    navigate('/user-dashboard');
  };

  const handleGoBack = () => {
    navigate('/user-dashboard');
  };

  if (!quiz) {
    return (
        <div style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '50vh',
          fontSize: '1.125rem',
          color: 'var(--quiz-text-muted)'
        }}>
          Cargando quiz...
        </div>
    );
  }

  const currentQuestion = quiz.steps[currentQuestionIndex];

  const footer = (
      <div className="navigation-buttons">
        <CustomButton
            label="Anterior"
            icon="pi pi-arrow-left"
            onClick={goToPreviousQuestion}
            disabled={currentQuestionIndex === 0}
            severity="secondary"
        />
        {currentQuestionIndex < quiz.steps.length - 1 && (
            <CustomButton
                label="Siguiente"
                icon="pi pi-arrow-right"
                iconPos="right"
                onClick={goToNextQuestion}
            />
        )}
        {currentQuestionIndex === quiz.steps.length - 1 && (
            <CustomButton
                label={isProcessingLlm ? "Procesando..." : "Enviar"}
                icon={isProcessingLlm ? "pi pi-spin pi-spinner" : "pi pi-check"}
                onClick={handleSubmitQuiz}
                className="p-button-success"
                disabled={!allQuestionsAnswered || isProcessingLlm}
            />
        )}
      </div>
  );

  return (
      <div className="quiz-taker-page">
        <Header />
        <Splitter className="quiz-taker-splitter">
          <SplitterPanel className="question-panel" size={70}>
            <CustomCard
                title={quiz.tema}
                subTitle={`Pregunta ${currentQuestionIndex + 1} de ${quiz.steps.length}`}
                footer={footer}
                className="question-card"
            >
              <div className={`question-transition ${isTransitioning ? 'changing' : ''}`}>
                <p className="question-text">{currentQuestion.texto}</p>
                {Array.isArray(currentQuestion.opciones) && currentQuestion.opciones.length > 0 ? (
                    <div className="quiz-options-group">
                      {currentQuestion.opciones.map((opcion, idx) => (
                          <label key={idx} className="quiz-option" style={{display: "block", margin: "0.5rem 0"}}>
                            <input
                                type="radio"
                                name={`respuesta_${currentQuestion.id}`}
                                value={opcion}
                                checked={answers[currentQuestion.id] === opcion}
                                onChange={handleAnswerChange}
                                style={{marginRight: "0.5rem"}}
                            />
                            {opcion}
                          </label>
                      ))}
                    </div>
                ) : (
                    <TextareaField
                        value={answers[currentQuestion.id] || ''}
                        onChange={handleAnswerChange}
                        rows={8}
                        autoResize
                        placeholder="Escribe tu respuesta aquí..."
                        className="answer-textarea"
                    />
                )}
              </div>
            </CustomCard>
          </SplitterPanel>
          <SplitterPanel className="navigation-panel" size={30}>
            <CustomCard title="Preguntas" className="navigation-card">
              <ul className="navigation-list">
                {quiz.steps.map((q, index) => (
                    <li
                        key={q.id}
                        className={`navigation-item ${index === currentQuestionIndex ? 'active' : ''}`}
                        onClick={() => handleQuestionTransition(index)}
                    >
                      {index + 1}. {q.texto}
                    </li>
                ))}
              </ul>
              
              <Divider />
              
              <div className="custom-prompt-section">
                <h4>Instrucciones personalizadas para el análisis</h4>
                <InputTextarea
                    value={customPrompt}
                    onChange={(e) => setCustomPrompt(e.target.value)}
                    rows={4}
                    autoResize
                    placeholder="Escribe instrucciones específicas para el análisis de tus respuestas (opcional)..."
                    className="custom-prompt-textarea"
                />
                <small className="help-text">
                  Si no especificas instrucciones, se realizará un análisis general de tus respuestas.
                </small>
              </div>
              
              <div className="back-button-container">
                <CustomButton
                    label="Volver al Dashboard"
                    icon="pi pi-home"
                    severity="secondary"
                    onClick={handleGoBack}
                />
              </div>
            </CustomCard>
          </SplitterPanel>
        </Splitter>
        <Dialog
            visible={showCompletionModal}
            onHide={handleCloseModal}
            header="¡Quiz completado!"
            footer={
              <CustomButton
                  label="Volver al Dashboard"
                  icon="pi pi-arrow-left"
                  onClick={handleCloseModal}
              />
            }
            style={{ width: '60vw', minWidth: 400, maxWidth: 800 }}
            modal
        >
          <div style={{ padding: "1rem" }}>
            <p>¡Gracias por completar el quiz!</p>
            
            {showLlmResult && (
              <div className="llm-response-section">
                <Divider />
                <h4>Análisis de tus respuestas:</h4>
                <div className="llm-response-content" style={{
                  background: '#f8f9fa',
                  padding: '1rem',
                  borderRadius: '8px',
                  border: '1px solid #e9ecef',
                  whiteSpace: 'pre-wrap',
                  fontFamily: 'inherit',
                  maxHeight: '400px',
                  overflowY: 'auto'
                }}>
                  {llmResponse}
                </div>
              </div>
            )}
            
            {isProcessingLlm && (
              <div className="processing-section">
                <Divider />
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <i className="pi pi-spin pi-spinner" />
                  <span>Analizando tus respuestas...</span>
                </div>
              </div>
            )}
          </div>
        </Dialog>
      </div>
  );
};

export default QuizTaker;