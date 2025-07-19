import '../../styles/quiz.css'

import { useState, useEffect } from "react";
import { DataView } from 'primereact/dataview';
import { QuizModal } from "../ui/QuizModal";
import { QuizDetail } from "./QuizDetail";
import { CustomButton } from '../ui/CustomButton';
import { CustomCard } from '../ui/CustomCard';
import { adaptQuizDefinition } from '../../adapters/quizAdapter';
import { useAuth } from '../../contexts/AuthContext'; // Importa el contexto de autenticación

export const QuizPanel = () => {
  const { user } = useAuth(); // Obtiene el usuario autenticado

  const initialQuizzes = [
    {
      documentId: '1',
      tema: 'Onboarding de Nuevos Talentos',
      steps: [
        { step: 1, id: '1', texto: 'Pregunta 1', opciones: [], random: true },
        { step: 2, id: '2', texto: 'Pregunta 2', opciones: [], random: false }
      ]
    },
    {
      documentId: '2',
      tema: 'Evaluación de Habilidades Técnicas',
      steps: [
        { step: 1, id: '1', texto: 'Pregunta A', opciones: [], random: false }
      ]
    }
  ].map(adaptQuizDefinition);

  const [quizzes, setQuizzes] = useState(initialQuizzes);
  const [selectedQuiz, setSelectedQuiz] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [editingQuiz, setEditingQuiz] = useState(null);

  useEffect(() => {
    const loadQuizzes = async () => {
      try {
        const baseUrl = import.meta.env.VITE_API_URL || '/api';
        const res = await fetch(`${baseUrl}/quiz/list`);
        const ids = await res.json();
        const defs = await Promise.all(
          ids.map(id => fetch(`${baseUrl}/quiz/${id}`).then(r => r.json()))
        );
        setQuizzes(defs);
      } catch (err) {
        console.error('Error loading quizzes', err);
      }
    };
    loadQuizzes();
  }, []);

  const handleSaveQuiz = async (quizData) => {
    try {
      if (editMode && editingQuiz) {
        // Actualiza quiz existente
        const baseUrl = import.meta.env.VITE_API_URL || '/api';
        const response = await fetch(`${baseUrl}/quiz/${editingQuiz.documentId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(quizData)
        });

        if (response.ok) {
          const updatedQuiz = await response.json();
          const adapted = adaptQuizDefinition(updatedQuiz);
          setQuizzes(prev => prev.map(quiz =>
            quiz.documentId === editingQuiz.documentId ? adapted : quiz
          ));
          setSelectedQuiz(adapted);
        } else {
          console.error('Error updating quiz');
        }
      } else {
        // Crea nuevo quiz
        const adapted = adaptQuizDefinition({ ...quizData, documentId: `${Date.now()}` });
        setQuizzes([...quizzes, adapted]);
      }

      setModalVisible(false);
      setEditMode(false);
      setEditingQuiz(null);
    } catch (error) {
      console.error('Error saving quiz:', error);
    }
  };

  const handleEditQuiz = (quiz) => {
    setEditingQuiz(quiz);
    setEditMode(true);
    setModalVisible(true);
  };

  const handleCreateQuiz = () => {
    setEditMode(false);
    setEditingQuiz(null);
    setModalVisible(true);
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
          {user?.isAdmin && (
            <CustomButton
              label="Crear Quiz"
              icon="pi pi-plus"
              severity="primary"
              onClick={handleCreateQuiz}
            />
          )}
        </div>
        <DataView className="quiz-list" value={quizzes} itemTemplate={itemTemplate} />
      </CustomCard>
      <CustomCard title="Detalles del Quiz" className="quiz-detail-card">
        <QuizDetail quiz={selectedQuiz} onEdit={handleEditQuiz} user={user} />
      </CustomCard>
      <QuizModal
        visible={modalVisible}
        onHide={() => {
          setModalVisible(false);
          setEditMode(false);
          setEditingQuiz(null);
        }}
        onSave={handleSaveQuiz}
        editMode={editMode}
        initialData={editingQuiz}
      />
    </div>
  );
}