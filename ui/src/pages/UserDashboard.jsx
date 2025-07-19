import '../styles/user-dashboard.css';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { DataView } from 'primereact/dataview';
import { TabView, TabPanel } from 'primereact/tabview';
import { Header } from '../components/layout/Header';
import { CustomButton } from '../components/ui/CustomButton';
import { useAuth } from '../contexts/AuthContext';
import { QuizPanel } from '../components/quiz/QuizPanel';

const UserDashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [quizzes, setQuizzes] = useState([]);
  const [quizResults, setQuizResults] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadQuizzes = async () => {
      try {
        const baseUrl = import.meta.env.VITE_API_URL;
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

  const loadQuizResults = async () => {
    if (!user?.isAdmin) return;
    
    setLoading(true);
    try {
      const baseUrl = import.meta.env.VITE_API_URL;
      const res = await fetch(`${baseUrl}/quiz/submission/results`);
      const results = await res.json();
      setQuizResults(results);
    } catch (err) {
      console.error('Error loading quiz results', err);
    } finally {
      setLoading(false);
    }
  };

  const quizItemTemplate = (quiz) => {
    return (
        <div className="user-dashboard-list-item" onClick={() => navigate(`/quiz/${quiz.documentId}`)}>
          <div className="quiz-item-info">
            <h5>{quiz.tema}</h5>
          </div>
          <CustomButton label="Comenzar Quiz" icon="pi pi-arrow-right" />
        </div>
    );
  };

  const resultItemTemplate = (result) => {
    return (
        <div className="quiz-result-item">
          <div className="result-info">
            <h6>{result.quizTema}</h6>
            <p><strong>Usuario:</strong> {result.usuario}</p>
            <p><strong>Fecha:</strong> {new Date(result.fecha).toLocaleString()}</p>
            <p><strong>Calificación:</strong> {result.calificacion ? `${result.calificacion}/10` : 'N/A'}</p>
          </div>
          <div className="result-details">
            <details>
              <summary>Ver respuestas</summary>
              <div className="result-responses">
                {Object.entries(result.respuestas || {}).map(([key, value]) => (
                  <p key={key}><strong>{key}:</strong> {value}</p>
                ))}
              </div>
            </details>
            <details>
              <summary>Ver resultado LLM</summary>
              <div className="result-llm">
                <p>{result.resultadoInferencia}</p>
              </div>
            </details>
          </div>
        </div>
    );
  };

  return (
      <div className="user-dashboard-container">
        <Header />
        <div className="user-dashboard-content">
          {user?.isAdmin ? (
            <TabView>
              <TabPanel header="Quizes Disponibles">
                <h2>Quizes Disponibles</h2>
                <DataView value={quizzes} itemTemplate={quizItemTemplate} />
              </TabPanel>
              <TabPanel header="Gestión de Quiz" leftIcon="pi pi-cog">
                <div className="admin-quiz-management">
                  <h2>Gestión de Quiz - Panel de Administración</h2>
                  <p style={{ marginBottom: '1rem', color: '#666' }}>
                    Crear, editar y administrar quizzes del sistema
                  </p>
                  <QuizPanel />
                </div>
              </TabPanel>
              <TabPanel header="Resultados de Quiz" leftIcon="pi pi-chart-bar">
                <div className="admin-results-section">
                  <div className="admin-header">
                    <h2>Resultados de Quiz - Panel de Administración</h2>
                    <CustomButton 
                      label="Actualizar Resultados" 
                      icon="pi pi-refresh" 
                      onClick={loadQuizResults}
                      loading={loading}
                    />
                  </div>
                  <DataView 
                    value={quizResults} 
                    itemTemplate={resultItemTemplate}
                    emptyMessage="No hay resultados disponibles. Haz clic en 'Actualizar Resultados' para cargar."
                  />
                </div>
              </TabPanel>
            </TabView>
          ) : (
            <>
              <h2>Quizes Disponibles</h2>
              <DataView value={quizzes} itemTemplate={quizItemTemplate} />
            </>
          )}
        </div>
      </div>
  );
};

export default UserDashboard;