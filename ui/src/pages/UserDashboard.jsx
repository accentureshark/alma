import '../styles/user-dashboard.css';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { DataView } from 'primereact/dataview';
import { Header } from '../components/layout/Header';
import { CustomButton } from '../components/ui/CustomButton';

const exampleQuizzes = [
  {
    documentId: '1',
    tema: 'Quiz de Bienvenida',
    steps: [{ step: 1, id: '1', texto: 'Pregunta 1' }]
  },
  {
    documentId: '2',
    tema: 'Evaluación de Desempeño',
    steps: [{ step: 1, id: '1', texto: 'Pregunta A' }]
  },
];


const UserDashboard = () => {
  const navigate = useNavigate();
  const [quizzes, setQuizzes] = useState([]);

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

  const itemTemplate = (quiz) => {
    return (
      <div className="user-dashboard-list-item" onClick={() => navigate(`/quiz/${quiz.documentId}`)}>
        <div className="quiz-item-info">

          <h5>{quiz.tema}</h5>

        </div>
        <CustomButton label="Comenzar Quiz" icon="pi pi-arrow-right" />
      </div>
    );
  };

  return (
    <div className="user-dashboard-container">
      <Header />
      <div className="user-dashboard-content">
        <h2>Quizes Disponibles</h2>
        <DataView value={quizzes} itemTemplate={itemTemplate} />
      </div>
    </div>
  );
};

export default UserDashboard;
