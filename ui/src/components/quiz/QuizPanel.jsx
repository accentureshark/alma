import '../../styles/quiz.css'

import { useState, useEffect } from "react";
import { DataView } from 'primereact/dataview';
import { QuizModal } from "../ui/QuizModal";
import { QuizDetail } from "./QuizDetail";
import { CustomButton } from '../ui/CustomButton';
import { CustomCard } from '../ui/CustomCard';
import { adaptQuizDefinition } from '../../adapters/quizAdapter';

export const QuizPanel = () => {

  const initialQuizzes = [
    {
      documentId: '1',
      tema: 'Onboarding de Nuevos Talentos',
      steps: [
        { step: 1, id: '1', texto: 'Pregunta 1', opciones: [] },
        { step: 2, id: '2', texto: 'Pregunta 2', opciones: [] }
      ]
    },
    {
      documentId: '2',
      tema: 'Evaluación de Habilidades Técnicas',
      steps: [
        { step: 1, id: '1', texto: 'Pregunta A', opciones: [] }
      ]
    }
  ].map(adaptQuizDefinition);

  const [quizzes, setQuizzes] = useState(initialQuizzes);
  const [selectedQuiz, setSelectedQuiz] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);

  useEffect(() => {
    const loadQuizzes = async () => {
      try {
        const res = await fetch('/api/quiz/list');
        const ids = await res.json();
        const defs = await Promise.all(
          ids.map(id => fetch(`/api/quiz/${id}`).then(r => r.json()))
        );
        setQuizzes(defs);
      } catch (err) {
        console.error('Error loading quizzes', err);
      }
    };
    loadQuizzes();
  }, []);

  const handleSaveQuiz = (quizData) => {
    const adapted = adaptQuizDefinition({ ...quizData, documentId: `${Date.now()}` });
    setQuizzes([...quizzes, adapted]);
    setModalVisible(false);
  };

  const itemTemplate = (quiz) => {
    return (
      <div className="quiz-list-item" onClick={() => setSelectedQuiz(quiz)}>
        <div className="quiz-item-info">

          <h5>{quiz.tema}</h5>
          <span>{quiz.steps.length} preguntas</span>
        </div>
        <CustomButton icon="pi pi-chevron-right" className="p-button-rounded p-button-text" severity="secondary" />
      </div>
    );
  };

return (
  <div className="quiz-panel-container">
    <CustomCard className="quiz-list-card">
      <div className="quiz-panel-header">
        <h2 style={{ display: 'inline-block', marginRight: '10px' }}>Listado de Quiz</h2>
        <CustomButton
          label="Crear Quiz"
          icon="pi pi-plus"
          severity="primary"
          onClick={() => setModalVisible(true)}
        />
      </div>
      <DataView className="quiz-list" value={quizzes} itemTemplate={itemTemplate} />
    </CustomCard>
    <CustomCard title="Detalles del Quiz" className="quiz-detail-card">
      <QuizDetail quiz={selectedQuiz} />
    </CustomCard>
    <QuizModal
      visible={modalVisible}
      onHide={() => setModalVisible(false)}
      onSave={handleSaveQuiz}
    />
  </div>
);
}

