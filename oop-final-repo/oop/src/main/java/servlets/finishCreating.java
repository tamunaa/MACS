package servlets;

import dataBase.QuizDAO;
import objects.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/finishCreating")
public class finishCreating extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizDAO quizDAO = (QuizDAO) request.getServletContext().getAttribute("quizDAO");
        Quiz quiz = quizDAO.getQuizByID(Integer.parseInt(request.getParameter("quizId")));
        request.getRequestDispatcher("quizPage.jsp?searchInput=" + quiz.getQuizName()).forward(request, response);
    }
}
