<%@ page import="objects.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Navbar</title>
    <!-- Add Bootstrap CSS link (adjust the URL if needed) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-4bw+/aepP/YC94hEpVNVgiZdgIC5+VKNBQNGCHeKRQN+PtmoHDEXuppvnDJzQIu9" crossorigin="anonymous">    <!-- Add a modern font (adjust the URL if needed) -->
    <link href="https://fonts.googleapis.com/css2?family=Quicksand:wght@300&display=swap" rel="stylesheet">

    <link rel="stylesheet" type="text/css" href="css/navbar.css">


</head>
<body>

<nav class="navbar navbar-expand-lg bg-body-tertiary animated-bg">
    <div class="container-fluid">
        <div id="navbarSupportedContent" class="d-flex justify-content-between w-100">

            <ul class="navbar-nav" >
                <li class="nav-item">
                    <a class="nav-link" data-bs-toggle="tooltip" data-bs-placement="top" title="Home" href="home.jsp">
                       <form action="NewsFeedServlet" method="GET">
                           <button class="homeButton"><i class="bi bi-house-door"></i> </button>
                       </form>
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link" data-bs-toggle="tooltip" data-bs-placement="top" title="Profile" href="profile.jsp?self=true">
                        <i class="bi bi-person"></i>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-bs-toggle="tooltip" data-bs-placement="top" title="Announcements" href="announcements.jsp">
                        <i class="bi bi-megaphone"></i>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-bs-toggle="tooltip" data-bs-placement="top" title="Popular Quizzes" href="popularQuizzes.jsp">
                        <i class="bi bi-star"></i>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" data-bs-toggle="tooltip" data-bs-placement="top" title="Recent Quizzes" href="recentQuizzes.jsp">
                        <i class="bi bi-clock"></i>
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link" data-bs-toggle="tooltip" data-bs-placement="top" title="Add Quiz" href="createQuiz.jsp">
                        <i class="bi bi-folder-plus"></i>
                    </a>
                </li>


                <%
                    boolean isAdmin = ((User)session.getAttribute("currUser")).isAdmin();
                    if (isAdmin){%>
                <li class="nav-item">
                    <a class="nav-link" data-bs-toggle="tooltip" data-bs-placement="top" title="admin area" href="admin.jsp">
                        <i class="bi bi-gear"></i>
                    </a>
                </li>
                <%}%>

            </ul>

            <form class="d-flex" role="search" action="SearchServlet" method="GET">
                <input name= "searchInput" class="form-control me-2 custom-input" type="search" placeholder="Search" aria-label="Search">
                <input name="self" value="false" type="hidden">
                <input name="type" value="userSearch" type="hidden">

                <button class="btn btn-outline-secondary custom-btn" type="submit">Search</button>
            </form>

            <ul class="navbar-nav">
                <li class="nav-item" data-bs-toggle="tooltip" data-bs-placement="top" title="Messages">
                    <a class="message-icon" href="messages.jsp">
                        <i class="bi bi-envelope"></i>
                    </a>
                </li>
                <li class="nav-item" data-bs-toggle="tooltip" data-bs-placement="top" title="Notifications">
                    <a class="notification-icon" onclick="toggleNotificationPanel()">
                        <i class="bi bi-bell"></i>
                    </a>
                </li>
                <li class="nav-item" data-bs-toggle="tooltip" data-bs-placement="top" title="Log Out">
                    <a class="log-out"  onclick="logOut()">
                        <i class="bi bi-box-arrow-right"></i>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<jsp:include page="notificationbar.jsp" />


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    });
</script>
</body>
</html>