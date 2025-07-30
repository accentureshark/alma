import '../../styles/quiz.css'

import { TabView, TabPanel } from 'primereact/tabview';
import { useState, useEffect } from "react";
import { DataView } from 'primereact/dataview';
import { QuizModal } from "../ui/QuizModal";
import { QuizDetail } from "./QuizDetail";
import { QuizResults } from "./QuizResults";
import { CustomButton } from '../ui/CustomButton';
import { CustomCard } from '../ui/CustomCard';
import { adaptQuizDefinition } from '../../adapters/quizAdapter';
import { useAuth } from '../../contexts/AuthContext';

export const QuizPanel = () => {
  const { user } = useAuth();

  const initialQuizzes = [];

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
        setQuizzes(defs.map(adaptQuizDefinition));
      } catch (err) {
        console.error('Error loading quizzes', err);
      }
    };
    loadQuizzes();
  }, []);

  const handleSaveQuiz = async (quizData) => {
    try {
      const baseUrl = import.meta.env.VITE_API_URL || '/api';

      if (editMode && editingQuiz) {
        const response = await fetch(`${baseUrl}/quiz/${editingQuiz.documentId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(quizData)
        });

        if (response.ok) {
          const updatedQuiz = await response.json();
          const adapted = adaptQuizDefinition(updatedQuiz);
          setQuizzes(prev =>
              prev.map(q => q.documentId === adapted.documentId ? adapted : q)
          );
          setSelectedQuiz(adapted);
        } else {
          console.error('Error updating quiz');
        }
      } else {
        const adapted = adaptQuizDefinition({
          ...quizData,
          documentId: `${Date.now()}`
        });
        setQuizzes(prev => [...prev, adapted]);
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

  const itemTemplate = (quiz) => (
      <div className="quiz-list-item" onClick={() => setSelectedQuiz(quiz)}>
        <div className="quiz-item-info">
          <h5>{quiz.tema}</h5>
          <span>{quiz.steps.length} preguntas</span>
        </div>
        <CustomButton icon="pi pi-chevron-right" className="p-button-rounded p-button-text" severity="secondary" />
      </div>
  );

  return (
      <div className="quiz-panel-container">
        <TabView>
          <TabPanel header="Gestión de Quizzes" leftIcon="pi pi-cog">
            <div className="quiz-panel-grid">
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

              {selectedQuiz && (
                <CustomCard title="Detalles del Quiz" className="quiz-detail-card">
                  <QuizDetail quiz={selectedQuiz} onEdit={handleEditQuiz} user={user} />
                </CustomCard>
              )}
            </div>

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
          </TabPanel>


          <TabPanel header="Resultados de Quiz" leftIcon="pi pi-chart-bar">
            <QuizResults quizzes={quizzes} />
          </TabPanel>
        </TabView>
      </div>
  );
};
