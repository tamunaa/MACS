package servlets;

import com.mysql.cj.Session;
import com.mysql.cj.xdevapi.JsonArray;
import dataBase.*;
import dataBase.questionsDAOs.QuestionsDAO;
import objects.History;
import objects.Quiz;
import objects.User;
import objects.questions.Question;
import objects.questions.QuestionResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.util.Date;

@WebServlet("/quiz")
public class quiz extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int quizId = Integer.parseInt(request.getParameter("quizId"));
        QuizDAO quizDAO = (QuizDAO) request.getServletContext().getAttribute("quizDAO");
        UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
        HistoryDAO historyDAO = (HistoryDAO) request.getServletContext().getAttribute("historyDAO");
        Quiz quiz = quizDAO.getQuizByID(quizId);
        String author = userDAO.getUsernameByID(quiz.getAuthor());

        List<History> historyList = historyDAO.getHistoryByQuizId(quizId);
        List<String> historyUsernames = new ArrayList<>();

        for (History history : historyList) {
            String username = userDAO.getUsernameByID(history.getUserId());
            historyUsernames.add(username);
        }

        HttpSession session = request.getSession();
        session.setAttribute("quiz", quiz);
        request.setAttribute("historyList", historyList);
        request.setAttribute("historyUsernames", historyUsernames);
        request.setAttribute("author", author);



        request.getRequestDispatcher("quiz.jsp?quizId=" + quizId).forward(request, response);


    }
}
