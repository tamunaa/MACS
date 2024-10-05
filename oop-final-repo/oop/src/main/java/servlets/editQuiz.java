package servlets;

import dataBase.DbQuizDAO;
import dataBase.QuizDAO;
import objects.Quiz;
import objects.questions.Question;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/editQuiz")
public class editQuiz extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizDAO quizDAO = (DbQuizDAO) request.getServletContext().getAttribute("quizDAO");

        int quizId = Integer.parseInt(request.getParameter("quizId"));
        List<Question> questions = quizDAO.getQuestions(quizId);
        request.setAttribute("questions", questions);

        request.getRequestDispatcher("editQuiz.jsp?quizId=" + quizId).forward(request, response);
    }
}
