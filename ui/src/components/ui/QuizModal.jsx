import { Dialog } from "primereact/dialog";
import { Button } from "primereact/button";
import { InputField } from "./InputField";
import { useState } from "react";

export const QuizModal = ({ visible, onHide, onSave }) => {
    const [quizTitle, setQuizTitle] = useState("");
    const [quizPrompt, setQuizPrompt] = useState("");
    const [questions, setQuestions] = useState([{ id: 1, value: "" }]);
    const MAX_QUESTIONS = 5;

    const addQuestion = () => {
        if (questions.length < MAX_QUESTIONS) {
            const newId = Math.max(...questions.map(q => q.id)) + 1;
            setQuestions(prev => [...prev, { id: newId, value: "" }]);
        }
    };

    const removeQuestion = (id) => {
        if (questions.length > 1) {
            setQuestions(prev => prev.filter(question => question.id !== id));
        }
    };

    const updateQuestion = (id, value) => {
        setQuestions(prev => 
            prev.map(question => 
                question.id === id ? { ...question, value } : question
            )
        );
    };

    const handleClose = () => {
        // Opcional: resetear todo al cerrar
        // setQuizTitle("");
        // setQuizPrompt("");
        // setQuestions([{ id: 1, value: "" }]);
        onHide();
    };

    return (
        <Dialog 
            header={`Crear Quiz ${quizTitle ? `- ${quizTitle}` : ''}`}
            visible={visible} 
            onHide={handleClose} 
            modal 
            draggable={false} 
            className="dialog quiz-modal"
            style={{ width: '50vw', minWidth: '400px', height: '700px' }}
        >
            <div className="quiz-content">
                {/* Título del Quiz */}
                <div className="quiz-title-section">
                    <label htmlFor="quiz-title" className="quiz-title-label">
                        <i className="pi pi-bookmark" style={{ marginRight: '0.5rem' }}></i>
                        Título del Quiz
                    </label>
                    <InputField
                        id="quiz-title"
                        value={quizTitle}
                        onChange={(e) => setQuizTitle(e.target.value)}
                        placeholder="Ingresa el título de tu quiz..."
                        className="quiz-title-input"
                    />
                </div>

                {/* Prompt del Quiz */}
                <div className="quiz-prompt-section">
                    <label htmlFor="quiz-prompt" className="quiz-prompt-label">
                        <i className="pi pi-comments" style={{ marginRight: '0.5rem' }}></i>
                        Prompt para LLM (opcional)
                    </label>
                    <textarea
                        id="quiz-prompt"
                        value={quizPrompt}
                        onChange={(e) => setQuizPrompt(e.target.value)}
                        placeholder="Ingresa el prompt que se usará para evaluar las respuestas con el LLM..."
                        className="quiz-prompt-input"
                        rows="3"
                    />
                </div>
                {questions.map((question, index) => (
                    <div key={question.id} className="question-item">
                        <div className="question-header">
                            <p className="question-label">Pregunta número {index + 1}</p>
                            {questions.length > 1 && (
                                <Button 
                                    icon="pi pi-trash" 
                                    className="p-button-rounded p-button-text p-button-danger p-button-sm"
                                    onClick={() => removeQuestion(question.id)}
                                    tooltip="Eliminar pregunta"
                                    tooltipOptions={{ position: 'top' }}
                                />
                            )}
                        </div>
                        <InputField 
                            value={question.value}
                            onChange={(e) => updateQuestion(question.id, e.target.value)}
                            placeholder={`Ingresa la pregunta ${index + 1}...`}
                            className="question-input"
                        />
                    </div>
                ))}
                
                <div className="add-question-section">
                    <Button
                        icon="pi pi-plus"
                        label={`Agregar Pregunta ${questions.length < MAX_QUESTIONS ? `(${MAX_QUESTIONS - questions.length} restantes)` : ''}`}
                        className="p-button-outlined"
                        onClick={addQuestion}
                        disabled={questions.length >= MAX_QUESTIONS}
                    />
                    {questions.length >= MAX_QUESTIONS && (
                        <small className="max-questions-warning">
                            <i className="pi pi-info-circle" style={{ marginRight: '0.25rem' }}></i>
                            Máximo {MAX_QUESTIONS} preguntas permitidas
                        </small>
                    )}
                </div>

                <div className="modal-footer">
                    <Button
                        label="Cancelar"
                        icon="pi pi-times"
                        className="p-button-outlined"
                        onClick={handleClose}
                    />
                    <Button
                        label="Guardar"
                        icon="pi pi-check"
                        className="p-button-primary"
                        severity="success" // Cambiado para mejor visibilidad
                        disabled={!quizTitle.trim() || questions.some(q => !q.value.trim())}
                        onClick={() => {
                            const quizData = {
                                tema: quizTitle,
                                prompt: quizPrompt,
                                steps: questions
                                    .filter(q => q.value.trim())
                                    .map((q, idx) => ({
                                        step: idx + 1,
                                        id: String(q.id),
                                        texto: q.value,
                                        opciones: []
                                    }))
                            };
                            onSave(quizData); // Llama a la función onSave
                            handleClose();
                        }}
                    />
                </div>
            </div>
        </Dialog>
    );
};