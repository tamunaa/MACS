package servlets;
import dataBase.MessageDAO;
import dataBase.UserDAO;
import objects.messages.*;
import objects.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;


@WebServlet("/MessengerServlet")
public class MessengerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse  response) throws ServletException, IOException {
        MessageDAO messageDAO= (MessageDAO) request.getServletContext().getAttribute("messageDAO");
        UserDAO userDAO= (UserDAO) request.getServletContext().getAttribute("userDAO");

        User sender = (User) request.getSession().getAttribute("currUser");
        int senderID = sender.getId();

        String receiver = request.getParameter("selectedUser");
        int receiverID = userDAO.getIDByUsername(receiver);

        String text = request.getParameter("messageText");

        Message message = new NoteMessage(senderID, receiverID, text, new Timestamp(System.currentTimeMillis()));
        messageDAO.addMessage(message);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
        if (request.getSession().getAttribute("currUser") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        int senderID = Integer.parseInt(request.getParameter("senderID"));
        User user = null;
        user = userDAO.getUserByUserId(senderID);
        request.setAttribute("user", user);
        request.getRequestDispatcher("messenger.jsp").forward(request,response);
    }
}