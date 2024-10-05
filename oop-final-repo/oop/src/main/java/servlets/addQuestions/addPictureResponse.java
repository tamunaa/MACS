package servlets.addQuestions;

import dataBase.DbQuizDAO;
import dataBase.QuizDAO;
import objects.questions.PictureResponse;
import objects.questions.Question;
import objects.questions.QuestionResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import dataBase.questionsDAOs.*;
@WebServlet("/addQuestions/addPictureResponse")
public class addPictureResponse extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuestionsDAO questionsDAO = (QuestionsDAO) request.getServletContext().getAttribute("questionsDAO");


        int quizId = Integer.parseInt(request.getParameter("quizId"));
        String questionText = request.getParameter("questionText");
        String pictureURL = request.getParameter("pictureURL");
        String answer = request.getParameter("answer");
        String timerStr = request.getParameter("timer");
        int timer = timerStr.equals("") ? 0 : Integer.parseInt(timerStr);

        Question question = new PictureResponse(questionText, pictureURL, answer);
        question.setTimer(timer);
        try {
            questionsDAO.addQuestion(question, quizId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("/editQuiz?quizId=" + quizId);
    }
}
