package servlets;
import dataBase.FriendsDAO;
import dataBase.UserDAO;
import objects.*;
import dataBase.FriendshipService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/FriendRequestServlet")
public class FriendRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        String username = request.getParameter("username");
        String friendRequestResponse = request.getParameter("response");
        User user = (User) request.getSession().getAttribute("currUser");
        UserDAO dao = (UserDAO) context.getAttribute("userDAO");
        User friend = dao.getUserByUsername(username);

        if(username == null || user == null || friend == null){
            request.setAttribute("mess", "the request could not be made");
            request.getRequestDispatcher("invalidUser.jsp").forward(request,response);
            return;
        }
        FriendsDAO friendsDAO = (FriendsDAO) context.getAttribute("friendsDAO");
        FriendshipService friendshipService = (FriendshipService) context.getAttribute("friendshipService");
        if(friendRequestResponse.equals("accept")){
            if(friendshipService.acceptRequest(user, friend, friendsDAO)){
                response.sendRedirect("profile.jsp?self=true");
            }else{
                request.setAttribute("mess", "request could not be accepted");
                request.getRequestDispatcher("invalidUser.jsp").forward(request,response);
            }
        }else if(friendRequestResponse.equals("reject")){
            if(friendshipService.rejectRequest(user, friend, friendsDAO)){
                response.sendRedirect("profile.jsp?self=true");
            }else{
                request.setAttribute("mess", "could not be ");
                request.getRequestDispatcher("invalidUser.jsp").forward(request,response);
            }
        }
    }

}
