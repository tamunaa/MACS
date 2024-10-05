package servlets;
import objects.User;
import dataBase.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@WebServlet(name = "RemoveAdminServlet", value = "/RemoveAdminServlet")
public class RemoveAdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currUser = (User) request.getSession().getAttribute("currUser");
        UserDAO userDAO = (UserDAO) getServletContext().getAttribute("userDAO");


        if (currUser == null || !currUser.isAdmin()) {
            return;
        }

        String username = request.getParameter("newAdmin");
        userDAO.removeAdmin(userDAO.getUserByUsername(username));
    }
}