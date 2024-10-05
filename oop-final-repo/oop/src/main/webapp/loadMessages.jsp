<%@ page import="dataBase.MessageDAO" %>
<%@ page import="objects.User" %>
<%@ page import="java.util.List" %>
<%@ page import="dataBase.UserDAO" %>
<%@ page import="objects.messages.Message" %>

<%
    MessageDAO messageDAO = (MessageDAO) request.getServletContext().getAttribute("messageDAO");
    UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");

    User currUser = (User) session.getAttribute("currUser");

    String selectedUser = request.getParameter("selectedUser");
    Integer selectedUserId = userDAO.getIDByUsername(selectedUser);

    List<Message> messages = messageDAO.getChat (selectedUserId, currUser.getId(), true);
%>
<div class="messages">
    <% for (Message message : messages) {
        int sender = message.getSenderID();
        String name = userDAO.getUserByUserId(sender).getUsername();
    %>

    <div class='message'>
        <p><%=name%>:  <%= message.getContent() %></p>
    </div>
    <%}%>
</div>
