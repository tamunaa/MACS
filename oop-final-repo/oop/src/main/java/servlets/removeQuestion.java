package servlets;

import dataBase.DbQuizDAO;
import dataBase.QuizDAO;
import dataBase.questionsDAOs.QuestionsDAO;
import objects.questions.Question;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/removeQuestion")
public class removeQuestion extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int questionId = Integer.parseInt(request.getParameter("questionId"));
        int quizId = Integer.parseInt(request.getParameter("quizId"));
        QuestionsDAO questionsDAO = (QuestionsDAO) request.getServletContext().getAttribute("questionsDAO");
        questionsDAO.removeQuestion(questionId);

        response.sendRedirect("editQuiz?quizId=" + quizId);
    }
}
