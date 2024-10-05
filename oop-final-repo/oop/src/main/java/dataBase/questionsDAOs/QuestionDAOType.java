package dataBase.questionsDAOs;

import objects.questions.Question;

interface QuestionDAOType {
    int addQuestion(Question question, int quizId);
    Question getQuestionByQuestionId(int questionId);
}
