package servlets;
import dataBase.FriendsDAO;
import dataBase.UserDAO;
import dataBase.FriendshipService;
import objects.User;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.HttpServlet.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "RemoveFriendServlet", value = "/RemoveFriendServlet")
public class RemoveFriendServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("shemodiis");
        ServletContext sce = request.getServletContext();
        HttpSession session = request.getSession();
        UserDAO userDAO = (UserDAO) sce.getAttribute("userDAO");
        String username = (String) request.getSession().getAttribute("username");

        System.out.println("username " + username);

        FriendshipService friendshipService = (FriendshipService) sce.getAttribute("friendshipService");
        User currentUser = (User) session.getAttribute("currUser");
        if(currentUser == null) {
            request.setAttribute("mess", "the user doesnt exist");
            try{
                request.getRequestDispatcher("/invalidUser.jsp").forward(request, response);
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
        }
        FriendsDAO friendsDAO = (FriendsDAO) sce.getAttribute("friendsDAO");
        boolean result = friendshipService.removeFriend(currentUser, username, friendsDAO, userDAO);
        String message = null;
        if(result == true){
            message = "Removed " + username + " from your friend list successfully.";
        }else {
            message = "Could not remove " + username + " from your friend list successfully." ;
        }
        request.getSession().setAttribute("error", message);
        response.sendRedirect("addFriend.jsp");
    }

}