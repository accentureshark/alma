import '../styles/user-dashboard.css';
import { useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { DataView } from 'primereact/dataview';
import { Header } from '../components/layout/Header';
import { CustomButton } from '../components/ui/CustomButton';

const UserDashboard = () => {
  const navigate = useNavigate();
  const [quizzes, setQuizzes] = useState([]);

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