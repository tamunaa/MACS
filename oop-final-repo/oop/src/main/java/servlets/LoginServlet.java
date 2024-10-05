package servlets;
import dataBase.*;
import objects.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/LoginServlet")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UserDAO userDAO = (UserDAO) getServletContext().getAttribute("userDAO");

        System.out.println("username "+ username + "password " + password);

        if(!userDAO.isValidUser(username, password)) {
            request.setAttribute("incorrect", true);
            request.setAttribute("mess", "Username Or Password Is Not Correct");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }
        User user = userDAO.getUserByUsername(username);

        String profileUrl = "profile.jsp?self=true";
        request.getSession().setAttribute("currUser", user);
        response.sendRedirect(profileUrl);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.sendRedirect("index.jsp");
    }
}


