export function adaptQuizDefinition(apiQuiz) {
  if (!apiQuiz) return null;
  const steps = (apiQuiz.steps || apiQuiz.questions || []).map((s, idx) => ({
    step: s.step ?? idx + 1,
    id: s.id ?? `q${idx + 1}`,
    texto: s.texto ?? s.value ?? '',
    opciones: s.opciones ?? s.options ?? [],
    random: s.random ?? false
  }));

  return {
    documentId: apiQuiz.documentId ?? apiQuiz.id,
    tema: apiQuiz.tema ?? apiQuiz.title,
    tipo: apiQuiz.tipo,
    version: apiQuiz.version,
    prompt: apiQuiz.prompt || '',
    steps
  };
}
