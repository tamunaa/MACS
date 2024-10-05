<%@ page import="objects.messages.Message" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="objects.messages.ChallengeMessage"%>
<%@ page import="dataBase.MessageDAO" %>
<%@ page import="objects.User" %>
<%@ page import="dataBase.UserDAO" %>
<%@ page import="objects.Challenge" %>
<%@ page import="dataBase.ChallengeDAO" %>

<%
    User currUser = (User) session.getAttribute("currUser");
    MessageDAO messageDAO = (MessageDAO) request.getServletContext().getAttribute("messageDAO");
    List<Message> notifications = messageDAO.getUsersRecentIncomingNotifications(currUser.getId());
    UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");

    ChallengeDAO challengeDAO = (ChallengeDAO) request.getServletContext().getAttribute("challengeDAO");
    List<Challenge> challenge = challengeDAO.getChallengeByChallengedID(currUser.getId());

%>

<div class="notification-wrapper">
    <div class="notification-panel" id="notificationPanel">

        <%for (int i = 0; i < notifications.size(); i++){
            Message notification = notifications.get(i);
            User sender = userDAO.getUserByUserId(notification.getSenderID());
            String pathToUser = "profile.jsp?self=false&&username="+sender.getUsername();
            %>
        <%if(notification.getType().equals("REQUEST")){%>
            <form class="notification" action="FriendRequestServlet" method="POST">
                You have a new friend request from <a href="<%= pathToUser %>"><%= sender.getUsername() %></a>
                <input type="hidden" name="username" value="<%= sender.getUsername() %>">
                <br>
                <button type="submit" name="response" value="accept">
                    accept
                </button>

                <button type="submit" name="response" value="reject">
                    reject
                </button>
            </form>

        <%}else{%>
            <%String path = "quizPage.jsp?searchInput=" + notification.getContent(); %>
            <form class="notification">
                your friend <a href="<%=pathToUser%>"> <%=sender.getUsername()%> </a>
                challenged you in <a href="<%=path%>" > <%=notification.getContent()%> </a>
            </form>
        <%}}%>
    </div>
</div>
