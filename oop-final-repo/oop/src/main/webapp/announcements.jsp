<%@ page import="dataBase.UserDAO" %>
<%@ page import="dataBase.AnnouncementDAO" %>
<%@ page import="objects.Announcement" %>
<%@ page import="java.util.List" %>
<%@ page import="objects.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" type="text/css" href="css/navbar.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="js/navbar.js"></script>

    <link rel="stylesheet" type="text/css" href="css/announcements.css">

    <title>Website with Announcements</title>
</head>
<body>

<jsp:include page="navbar.jsp" />
<jsp:include page="notificationbar.jsp" />

<%
    UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");

    AnnouncementDAO announcementDAO = (AnnouncementDAO) request.getServletContext().getAttribute("announcementDAO");

    List<Announcement> announcements =  announcementDAO.getAllAnnouncements();
%>

<div class="feed">
    <%
        for(int i = 0; i < announcements.size(); i++){
        Announcement announcement = announcements.get(i);
        User creator = userDAO.getUserByUserId(announcement.getCreatorId());
        String name = creator.getUsername();
        String path = "profile.jsp?self=false&&username="+name;
    %>
        <div class="announcement">
            <h3> <%= announcement.getTitle() %></h3>
            <p><%=announcement.getText()%></p>
            <p> created by: <a href="<%=path%>"> <%=name%> </a></p>
            <p class="timestamp">Posted on: August 11, 2023</p>
        </div>
    <%
    }
    %>
</div>
<footer>
    <!-- Your footer content here -->
</footer>
</body>
</html>