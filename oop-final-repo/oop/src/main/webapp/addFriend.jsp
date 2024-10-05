<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Friend</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/newStyles.css">
</head>
<body>
<header class="top-header">
</header>
<nav class="navbar">
    <%--    <ul class="nav-list">--%>
    <%--        <li class="nav-item">--%>
    <%--            <a href="addFriend.jsp" class="nav-link">Add Friend</a>--%>
    <%--        </li>--%>
    <%--        <li class="nav-item">--%>
    <%--            <a href="index.jsp" class="nav-link">Home</a> //ამაზე ვერ ჩამოვყალიბდი იყოს ცალკე index.jsp თუ პირდაპირ login.jsp--%>
    <%--        </li>--%>
    <%--        <li class="nav-item">--%>
    <%--            <a href="friends.jsp" class="nav-link">Friends</a>--%>
    <%--        </li>--%>
    <%--        <li class="nav-item">--%>
    <%--            <a href="friendRequests.jsp" class="nav-link">Friend requests</a>--%>
    <%--        </li>--%>
    <%--    </ul>--%>
</nav>

<div class="content-container">
    <% if (request.getSession().getAttribute("error") != null) { %>
    <p class="error-message"><%= request.getSession().getAttribute("error") %></p>
    <a href="profile.jsp?self=true"> go back to profile </a>
    <% } %>

    <%--    <form action="SendFriendRequestServlet" method="get" class="form">--%>
    <%--        <div class="form-group">--%>
    <%--            <input type="text" name="username" id="username" class="form-input" placeholder="Username">--%>
    <%--        </div>--%>
    <%--        <button class="form-button" type="submit">Send Friend Request</button>--%>
    <%--    </form>--%>

    <%--    <form action="RemoveFriendServlet" method="get" class="form">--%>
    <%--        <div class="form-group">--%>
    <%--            <input type="text" name="username" id="username2" class="form-input" placeholder="Username">--%>
    <%--        </div>--%>
    <%--        <button class="form-button" type="submit">Remove Friend</button>--%>
    <%--    </form>--%>
</div>

<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
</body>
</html>
