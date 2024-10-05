package servlets;

import dataBase.DbAchievementDAO;
import dataBase.DbQuizDAO;
import dataBase.FriendsDAO;
import dataBase.HistoryDAOSQL;
import objects.Achievement;
import objects.History;
import objects.Quiz;
import objects.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

//@WebServlet(name = "NewsFeedServlet", value = "/NewsFeedServlet")
@WebServlet("/NewsFeedServlet")
public class NewsFeedServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FriendsDAO friendsDAO = (FriendsDAO) request.getServletContext().getAttribute("friendsDAO");
        HistoryDAOSQL historyDAO = (HistoryDAOSQL) request.getServletContext().getAttribute("historyDAO");
        DbQuizDAO quizDAO = (DbQuizDAO) request.getServletContext().getAttribute("quizDAO");
        DbAchievementDAO achievementDAO = (DbAchievementDAO) request.getServletContext().getAttribute("achievementDAO");
        User currUser = (User) request.getSession().getAttribute("currUser");
        List<User> friends = friendsDAO.getAllFriends(currUser);
        List<History> takenQuizzes = new ArrayList<>();

        for (User friend : friends) {
            takenQuizzes.addAll(historyDAO.getHistoryByUserId(friend.getId()));
        }
        takenQuizzes.sort(Comparator.comparing(History::getDateTaken));
        Collections.reverse(takenQuizzes);


        request.setAttribute("friends", friends);
        request.setAttribute("quizzesTakenByFriends", takenQuizzes);

        List<Quiz> createdQuizzes = new ArrayList<>();
        for (User friend : friends) {
            createdQuizzes.addAll(quizDAO.getQuizzesByAuthor(friend.getId()));
        }
        createdQuizzes.sort(Comparator.comparing(Quiz::getDateCreated));
        Collections.reverse(takenQuizzes);
        request.setAttribute("quizzesCreatedByFriends", createdQuizzes);

        request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}
