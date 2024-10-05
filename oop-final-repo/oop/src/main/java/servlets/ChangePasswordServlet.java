package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import dataBase.*;
import objects.User;


@WebServlet(name = "ChangePasswordServlet", value = "/ChangePasswordServlet")
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userUsername = (String) ((User)request.getSession().getAttribute("currUser")).getUsername();
        String password = request.getParameter("password");

        if(userUsername == null || password == null){
            try{
                request.getRequestDispatcher("invalidUser.jsp").forward(request, response);
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
        }
        UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
            boolean res = userDAO.changePassword(userUsername, password);
        if(res){
            response.sendRedirect("profile.jsp?self=true");
        }else{
            request.setAttribute("incorrect", true);
            request.setAttribute("mess", "Failed to update password. Please try again.");
            request.getRequestDispatcher("invalidUser.jsp").forward(request,response);
        }
    }

}