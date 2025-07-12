import '../styles/quiz-taker.css'
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { Dialog } from 'primereact/dialog';
import { Header } from '../components/layout/Header';
import { CustomButton } from '../components/ui/CustomButton';
import { TextareaField } from '../components/ui/TextareaField';
import { CustomCard } from '../components/ui/CustomCard';

// Datos de ejemplo
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

  useEffect(() => {

    const foundQuiz = exampleQuizzes.find((q) => q.documentId === quizId);
    setQuiz(foundQuiz);

    const fetchQuiz = async () => {
      try {
        const res = await fetch(`/api/quiz/${quizId}`);
        const data = await res.json();
        setQuiz(data);
      } catch (err) {
        console.error('Error loading quiz', err);
      }
    };
    fetchQuiz();
  }, [quizId]);

  useEffect(() => {
    if (quiz) {
      const allAnswered = quiz.steps.every(

        (s) => answers[s.id] && answers[s.id].trim() !== ''

      );
      setAllQuestionsAnswered(allAnswered);
    }
  }, [answers, quiz]);

  const handleAnswerChange = (e) => {
    setAnswers({ ...answers, [currentQuestion.id]: e.target.value });
  };

  const handleQuestionTransition = (newIndex) => {
    setIsTransitioning(true);
    setTimeout(() => {
      setCurrentQuestionIndex(newIndex);
      setIsTransitioning(false);
    }, 150);
  };

  if (!quiz) {
    return (
      <div className="quiz-taker-page">
        <Header />
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
      </div>
    );
  }

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

  const currentQuestion = quiz.steps[currentQuestionIndex];


  const handleSubmitQuiz = () => {
    setShowCompletionModal(true);

  };

  const handleCloseModal = () => {
    setShowCompletionModal(false);
    navigate('/user-dashboard');
  };

  const handleGoBack = () => {
    navigate('/user-dashboard');
  };

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
          label="Enviar"
          icon="pi pi-check"
          onClick={handleSubmitQuiz}
          className="p-button-success"
          disabled={!allQuestionsAnswered}
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
              <TextareaField
                value={answers[currentQuestion.id] || ''}
                onChange={handleAnswerChange}
                rows={8}
                autoResize
                placeholder="Escribe tu respuesta aquí..."
                className="answer-textarea"
              />
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
            <div className="back-button-container">
              <CustomButton
                label="Volver a Quizzes"
                icon="pi pi-arrow-left"
                onClick={handleGoBack}
                className="back-button"
              />
            </div>
          </CustomCard>
        </SplitterPanel>
      </Splitter>

      <Dialog
        header="Quiz Completado"
        visible={showCompletionModal}
        onHide={handleCloseModal}
        modal
        footer={<CustomButton label="Aceptar" onClick={handleCloseModal} />}
      >
        <p>¡Has completado el quiz exitosamente!</p>
      </Dialog>
    </div>
  );
};

export default QuizTaker;