package servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import dataBase.*;
import objects.User;


@WebServlet(name = "ChangeUsernameServlet", value = "/ChangeUsernameServlet")
public class ChangeUsernameServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currUser = (User) request.getSession().getAttribute("currUser");
        String userEmail = currUser.getEmail();
        String username = request.getParameter("newUsername");

        if(userEmail == null || username == null){
            try{
                request.getRequestDispatcher("invalidUser.jsp").forward(request, response);
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
            return;
        }
        UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
        boolean res = userDAO.changeUsername(currUser, username);
        if(res){
            response.sendRedirect("profile.jsp?self=true");
        }else{
            request.setAttribute("incorrect", true);
            request.setAttribute("mess", "Failed to update username. Please try again.");
            request.getRequestDispatcher("invalidUser.jsp").forward(request,response);
        }
    }

}