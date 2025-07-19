import '../styles/home.css';
import { Header } from '../components/layout/Header';
import { QuizPanel } from '../components/quiz/QuizPanel';
import { useAuth } from '../contexts/AuthContext';

const Home = () => {
    const { user } = useAuth();

    return (
        <div className="home-container">
            <Header />
            <main className="home-main">
                {user?.isAdmin ? (
                    <QuizPanel />
                ) : (
                    <p style={{ color: '#888', padding: '2rem' }}>
                        Acceso denegado. Esta sección es solo para administradores.
                    </p>
                )}
            </main>
        </div>
    );
};

export default Home;
