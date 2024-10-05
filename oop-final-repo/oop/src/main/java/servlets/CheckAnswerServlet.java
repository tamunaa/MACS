package servlets;

import com.mysql.cj.conf.ConnectionUrlParser;
import dataBase.QuizDAO;
import dataBase.questionsDAOs.QuestionsDAO;
import dataBase.questionsDAOs.ResponseDAO;
import objects.Quiz;
import objects.questions.Question;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CheckAnswerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        QuizDAO quizDAO = (QuizDAO) request.getServletContext().getAttribute("quizDAO");
        QuestionsDAO questionsDAO = (QuestionsDAO) request.getServletContext().getAttribute("questionsDAO");
        Quiz currQuiz = (Quiz) request.getSession().getAttribute("currQuiz");
        //TODO: set current quiz as atribute when quiz starts
        Question question = (Question) request.getSession().getAttribute("question");
        String answer = (String) request.getSession().getAttribute("answer");
        //TODO: yvela quizis dawyebisas sheiqmnas da sesias daesetos axali hashmap
        HashMap<Question, String> questionAnswer = (HashMap<Question, String>) request.getSession().getAttribute("questionAnswer");
        questionAnswer.put(question, answer);
        if(currQuiz.correctImmediately()){
            int score = question.evaluate(answer);
            int max_score = question.getCorrectAnswers().length;
            request.setAttribute("score", score + " / " + max_score);
            request.setAttribute("correctAnswer", question.getCorrectAnswers());
//            response.sendRedirect(რა კითხვის ფეიჯის იყო იმას რო გამოაჩინოს ქულა);
            response.sendRedirect(question.getQuestionType() + ".jsp");
        }
    }
}
