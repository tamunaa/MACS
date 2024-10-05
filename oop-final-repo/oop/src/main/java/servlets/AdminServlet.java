package servlets;

import dataBase.AnnouncementDAOSQL;
import dataBase.QuizDAO;
import dataBase.UserDAO;
import objects.Announcement;
import objects.Quiz;
import objects.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Timestamp;

@WebServlet(name = "AdminServlet", value = "/AdminServlet")
public class AdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!((User) request.getSession().getAttribute("currUser")).isAdmin()) {
            response.sendRedirect("profile.jsp?self=true");
            return;
        }
        User currUser = ((User) request.getSession().getAttribute("currUser"));
        String type = request.getParameter("type");


        if (type.equals("user")) {
            UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
            if (request.getParameter("deleteUser") != null) {
                String deleteUserName = (String) request.getParameter("deleteUser");
                int userId = userDAO.getIDByUsername(deleteUserName);
                userDAO.removeUser(userId);
            }
        }else if (type.equals("quiz")){
            QuizDAO quizDAO = (QuizDAO) request.getServletContext().getAttribute("quizDAO");
            String deleteQuizName = request.getParameter("deleteQuiz");
            int quizId = ((Quiz)quizDAO.getQuizByQuizName(deleteQuizName).get(0)).getID();
            quizDAO.removeQuiz(quizId);
        }else if (type.equals("addCategory")){
            String category = request.getParameter("categoryName");
            UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
            userDAO.addCategory(currUser.getId(), category);
        }
        request.getRequestDispatcher("admin.jsp").forward(request, response);
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!((User) request.getSession().getAttribute("currUser")).isAdmin()) {
            response.sendRedirect("profile.jsp?self=true");
            return;
        }
        AnnouncementDAOSQL announcementDAO = (AnnouncementDAOSQL) request.getServletContext().getAttribute("announcementDAO");

        if (request.getParameter("announcementTitle") != null) {
            String announcementTitle = request.getParameter("announcementTitle");
            String announcementText = request.getParameter("announcementText");
            announcementDAO.insertAnnouncement(new Announcement((((User) request.getSession().getAttribute("currUser")).getId()), new Timestamp(new java.util.Date().getTime()), announcementTitle, announcementText));
        }
        request.getRequestDispatcher("admin.jsp").forward(request, response);
    }
}

