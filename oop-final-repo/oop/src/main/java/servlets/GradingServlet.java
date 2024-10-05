package servlets;


import com.mysql.cj.conf.ConnectionUrlParser;
import dataBase.UserDAO;
import dataBase.questionsDAOs.GradeDAO;
import dataBase.questionsDAOs.ResponseDAO;
import objects.QuestionResponsePair;
import objects.Response;
import objects.User;
import objects.questions.GradedQuestion;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "GradingServlet", value = "/GradingServlet")
public class GradingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the list of questions from the database
        ResponseDAO responseDAO = (ResponseDAO) getServletContext().getAttribute("responseDAO");
        List<QuestionResponsePair> questionResponsePairs;
        try {
            User currUser = (User) request.getSession().getAttribute("currUser");
            int userID = currUser.getId();
            questionResponsePairs = responseDAO.getUngradedResponsesByAuthorID(userID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        request.getSession().setAttribute("responses", questionResponsePairs);

        request.getRequestDispatcher("grading.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<QuestionResponsePair> questionResponsePairs = (List<QuestionResponsePair>) request.getSession().getAttribute("responses");

        ResponseDAO responseDAO = (ResponseDAO) getServletContext().getAttribute("responseDAO");
        GradeDAO gradeDAO = (GradeDAO) getServletContext().getAttribute("gradeDAO");
        System.out.println(responseDAO);
        // Iterate through the pairs and update the database
        for (QuestionResponsePair pair : questionResponsePairs) {
            int responseId = pair.getResponse().getId();
            System.out.println(responseId);
            int score = pair.getResponse().getGrade();
            System.out.println(score);
            boolean isGraded = pair.getResponse().isGraded();
            if(isGraded)System.out.println("true");
            if(!isGraded)System.out.println("false");
            // Update the response record in the database
            responseDAO.addScoreAndMarkAsGraded(responseId, score, true);
            int historyId = pair.getResponse().getHistoryId();
            gradeDAO.updateScore(historyId);
        }

        response.sendRedirect("gradingFinished.jsp");
    }


}


