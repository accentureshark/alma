-- Add prompt column to existing quiz_definition table
ALTER TABLE quiz.quiz_definition ADD COLUMN IF NOT EXISTS prompt TEXT;