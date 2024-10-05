package servlets;

import dataBase.ChallengeDAO;
import dataBase.QuizDAO;
import objects.Challenge;
import objects.messages.*;
import objects.User;
import dataBase.UserDAO;
import dataBase.MessageDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "NotificationServlet", value = "/NotificationServlet")
public class NotificationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currUser = (User) request.getSession().getAttribute("currUser");
        if (currUser == null) return;
        String recipient = request.getParameter("recipient");

        String type = request.getParameter("type");
        String content = request.getParameter("content");


        if (recipient == null || type == null) return;
        if (type.equals("CHALLENGE") && content == null) return;
        if (type.equals("CHALLENGE") && content.isEmpty()) return;

        UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
        User recipientUser = userDAO.getUserByUsername(recipient);
        if (recipientUser == null || recipientUser.getId() == currUser.getId()) {
            return;
        }
        MessageDAO messageDAO = (MessageDAO) request.getServletContext().getAttribute("messageDAO");
        Message newNotification = null;
        if(type.equals("CHALLENGE")) {
            newNotification = new ChallengeMessage(currUser.getId(),recipientUser.getId(),content);
            QuizDAO quizDAO = (QuizDAO) request.getServletContext().getAttribute("quizDAO");
            ChallengeDAO challengeDAO = (ChallengeDAO) request.getServletContext().getAttribute("challengeDAO");
            String quizName = request.getParameter("quizName");
            Challenge challenge = new Challenge(currUser.getId(), recipientUser.getId(), quizDAO.getQuizByQuizName(quizName).get(0).getID());
            challengeDAO.addChallenge(challenge);

        }else if(type.equals("REQUEST")) {
            newNotification = new RequestMessage(currUser.getId(),recipientUser.getId());
        }

        messageDAO.addMessage(newNotification);
        String quizName = request.getParameter("quizName");
        String path = "quizPage.jsp?searchInput="+quizName;
        response.sendRedirect(path);
    }
}