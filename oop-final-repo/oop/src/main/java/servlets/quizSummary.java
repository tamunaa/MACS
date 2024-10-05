package servlets;

import dataBase.AchievementDAO;
import dataBase.DbQuizDAO;
import dataBase.HistoryDAO;
import dataBase.QuizDAO;
import objects.Quiz;
import objects.User;
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

@WebServlet("/quizSummary")
public class quizSummary extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HistoryDAO historyDAO = (HistoryDAO) request.getServletContext().getAttribute("historyDAO");
        AchievementDAO achievementDAO = (AchievementDAO) request.getServletContext().getAttribute("achievementDAO");
        int userId = ((User)request.getSession().getAttribute("currUser")).getId();
        if(historyDAO.getHistoryByUserId(userId).size() >= 10){
            achievementDAO.addUserAchievement(userId, 6);
        }
        request.getSession().setAttribute("endTime", new Timestamp(new java.util.Date().getTime()));
        QuizDAO quizDAO = (QuizDAO) request.getServletContext().getAttribute("quizDAO");
        if(quizDAO.getQuizByID(Integer.parseInt(request.getParameter("quizId"))).isPractice()){
            achievementDAO.addUserAchievement(userId, 3);
        }
        if(historyDAO.sortedHistory(Integer.parseInt(request.getParameter("quizId")),"score", 1).size() > 0) {
            if (historyDAO.sortedHistory(Integer.parseInt(request.getParameter("quizId")), "score", 1).get(0).getUserId() == userId) {
                achievementDAO.addUserAchievement(userId, 2);
            }
        }
        request.getRequestDispatcher("quizResult.jsp?quizId=" + request.getParameter("quizId")).forward(request, response);
    }

}