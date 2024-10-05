package servlets;

import dataBase.DbQuizDAO;
import dataBase.QuizDAO;
import objects.Quiz;
import objects.questions.*;
import org.apache.commons.dbcp2.BasicDataSource;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Timestamp;


@WebServlet("/loadQuizQuestions")
public class loadQuizQuestions extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizDAO quizDAO = (DbQuizDAO) request.getServletContext().getAttribute("quizDAO");
        int quizId = Integer.parseInt(request.getParameter("quizId"));

        List<Question> questionsList = quizDAO.getQuestions(quizId);
        Quiz quiz = quizDAO.getQuizByID(quizId);
        String path = quiz.isOnOnePage() ? "singlePageQuiz.jsp?quizId=" + quizId : "multiplePageQuiz.jsp?quizId=" + quizId;

        Question[] questions = new Question[questionsList.size()];
        questions = questionsList.toArray(questions);

        request.getSession().setAttribute("questions", questions);
        request.getRequestDispatcher(path).forward(request, response);
    }
}
