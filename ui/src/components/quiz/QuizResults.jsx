import { useState } from 'react';
import ReactMarkdown from 'react-markdown';
import { InputField } from '../ui/InputField';
import { CustomButton } from '../ui/CustomButton';
import { DataView } from 'primereact/dataview';

export const QuizResults = ({ quizzes }) => {
    const [quizResults, setQuizResults] = useState([]);
    const [filterUser, setFilterUser] = useState('');
    const [filterQuiz, setFilterQuiz] = useState('');
    const [loading, setLoading] = useState(false);

    const loadQuizResults = async () => {
        const baseUrl = import.meta.env.VITE_API_URL;
        let results = [];

        try {
            setLoading(true);
            if (filterUser) {
                const res = await fetch(`${baseUrl}/quiz/submission/results/user/${encodeURIComponent(filterUser)}`);
                results = await res.json();
                if (filterQuiz) {
                    results = results.filter(r => r.quizDocumentId === filterQuiz);
                }
            } else if (filterQuiz) {
                const res = await fetch(`${baseUrl}/quiz/submission/results/quiz/${encodeURIComponent(filterQuiz)}`);
                results = await res.json();
            } else {
                const res = await fetch(`${baseUrl}/quiz/submission/results`);
                results = await res.json();
            }
            setQuizResults(results);
        } catch (err) {
            console.error('Error loading quiz results', err);
        } finally {
            setLoading(false);
        }
    };

    const resultItemTemplate = (result) => (
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
                    <div className="result-llm" style={{
                        background: '#f8f9fa',
                        padding: '1rem',
                        borderRadius: '8px',
                        border: '1px solid #e9ecef',
                        marginTop: '0.5rem'
                    }}>
                        <ReactMarkdown>{result.resultadoInferencia}</ReactMarkdown>
                    </div>
                </details>
            </div>
        </div>
    );

    return (
        <div className="admin-results-section">
            <div className="admin-header">
                <h2>Resultados de Quiz - Panel de Administración</h2>
                <div className="admin-filters">
                    <InputField
                        value={filterUser}
                        onChange={e => setFilterUser(e.target.value)}
                        placeholder="Usuario"
                        className="filter-input"
                    />
                    <select
                        value={filterQuiz}
                        onChange={e => setFilterQuiz(e.target.value)}
                        className="filter-select"
                    >
                        <option value="">Todos los Quizzes</option>
                        {quizzes.map(q => (
                            <option key={q.documentId} value={q.documentId}>{q.tema}</option>
                        ))}
                    </select>
                    <CustomButton
                        label="Filtrar"
                        icon="pi pi-search"
                        onClick={loadQuizResults}
                        loading={loading}
                    />
                    <CustomButton
                        label="Actualizar"
                        icon="pi pi-refresh"
                        onClick={() => { setFilterUser(''); setFilterQuiz(''); loadQuizResults(); }}
                        loading={loading}
                        severity="secondary"
                    />
                </div>
            </div>
            <DataView
                value={quizResults}
                itemTemplate={resultItemTemplate}
                emptyMessage="No hay resultados disponibles. Haz clic en 'Actualizar Resultados' para cargar."
            />
        </div>
    );
};
