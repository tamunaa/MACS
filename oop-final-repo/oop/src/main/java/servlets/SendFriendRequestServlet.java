package servlets;
import dataBase.*;
import objects.User;
import objects.messages.*;
import static dataBase.FriendshipService.RequestResult.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SendFriendRequestServlet", value = "/SendFriendRequestServlet")
public class SendFriendRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext sc = request.getServletContext();
        String username = (String) request.getSession().getAttribute("username");
        UserDAO userDAO = (UserDAO) sc.getAttribute("userDAO");
        MessageDAO messageDAO = (MessageDAO) sc.getAttribute("messageDAO");
        FriendshipService friendshipService = (FriendshipService) sc.getAttribute("friendshipService");
        User currentUser = (User) request.getSession().getAttribute("currUser");

        if(currentUser == null){
            request.setAttribute("mess", "the user doesnt exist");
            try{
                request.getRequestDispatcher("/invalidUser.jsp").forward(request, response);
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }
            return;
        }

        FriendsDAO friendsDAO = (FriendsDAO) sc.getAttribute("friendsDAO");
        FriendshipService.RequestResult result = friendshipService.sendRequest(currentUser, username, friendsDAO,userDAO);
        String message = null;
        if(result == REQUEST_SUCCESS){
            Message req = new RequestMessage(currentUser.getId(), userDAO.getIDByUsername(username));
            messageDAO.addMessage(req);
            message = "Friend request sent to " + username + " successfully.";
        }else if(result == REQUEST_ALREADY_EXISTS){
            message = "You already have sent friend request to " + username + ".";
        }else if(result == REQUEST_BECAME_FRIENDS){
            message = "You and " + username + " became friends!";
        }else if(result == REQUEST_INVALID_USER){
            message = REQUEST_INVALID_USER + " Please enter a valid user";
        }else if(result == REQUEST_ARE_FRIENDS){
            message = "You and " + username + " already are friends! Please enter a different username";
        }else if(result == REQUEST_SAME_USER){
            message = "You can't become friends with yourself! Please enter a different username";
        }
        System.out.println(message);
        request.getSession().setAttribute("error", message);
        response.sendRedirect("addFriend.jsp");
    }
}