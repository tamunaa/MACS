<%@ page import="java.util.List" %>
<%@ page import="objects.User" %>
<%@ page import="objects.History" %>
<%@ page import="objects.Quiz" %>
<%@ page import="objects.Achievement" %>
<%@ page import="dataBase.UserDAO" %>
<%@ page import="dataBase.QuizDAO" %>
<%@ page import="dataBase.AchievementDAO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Main Page</title>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" type="text/css" href="css/navbar.css">
    <script src="js/navbar.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <link rel="stylesheet" type="text/css" href="css/home.css">

</head>
<body>

<%
    List<User> friends = (List<User>) request.getAttribute("friends");
    List<History> quizzesTakenByFriends = (List<History>) request.getAttribute("quizzesTakenByFriends");
    List<Quiz> createdQuizzes = (List<Quiz>) request.getAttribute("quizzesCreatedByFriends");

    UserDAO userDao = (UserDAO) request.getServletContext().getAttribute("userDAO");
    QuizDAO quizDao = (QuizDAO) request.getServletContext().getAttribute("quizDAO");
    AchievementDAO achievementDAO = (AchievementDAO) request.getServletContext().getAttribute("achievementDAO");
%>



<jsp:include page="navbar.jsp" />
<jsp:include page="notificationbar.jsp" />

<div class="feed">
    <h1 class="text-center">News Feed</h1>

    <div class="feed-container">

        <%for (int i = 0; i < quizzesTakenByFriends.size(); i++){
            History cur = quizzesTakenByFriends.get(i);
            int friend = cur.getUserId();
            String friendName = userDao.getUsernameByID(friend);
            String pathToFriendProfile = "profile.jsp?self=false&&username="+friendName;
            int score = cur.getScore();
            String quizName = quizDao.getQuizByID(cur.getQuizId()).getQuizName();
            String pathToQuizPage = "quizPage.jsp?searchInput="+quizName;
        %>
            <div class="feed-item">
                <p>
                    <strong>
                        <a href="<%=pathToFriendProfile%>"><%=friendName%></a>
                    </strong>
                    took <a href="<%=pathToQuizPage%>"> <%=quizName%> </a>
                    and scored <%=score%>
                </p>
                <p style="color: lightslategray; font-size: 14px"> at  <%=cur.getDateTaken()%> </p>
            </div>
        <%}%>


        <%for(Quiz cur : createdQuizzes){
            String friendName = userDao.getUsernameByID(cur.getAuthor());
            String pathToFriendProfile = "profile.jsp?self=false&&username="+friendName;
            String quizName = cur.getQuizName();
            String pathToQuizPage = "quizPage.jsp?searchInput="+quizName;
        %>
            <div class="feed-item">
                <p>
                    <strong>
                        <a href="<%=pathToFriendProfile%>"><%=friendName%></a>
                    </strong>
                    created <a href="<%=pathToQuizPage%>"> <%=quizName%> </a>
                </p>
                <p style="color: lightslategray; font-size: 14px"> at  <%=cur.getDateCreated()%> </p>
            </div>
        <%}%>

        <%for(User cur : friends){
            List<Achievement> achievements = achievementDAO.getUserAchievements(cur.getId());
            String friendName = cur.getUsername();
            String pathToFriendProfile = "profile.jsp?self=false&&username="+friendName;


        %>
            <%for(Achievement curAchievement : achievements){%>
                <div class="feed-item">
                    <p>
                        <strong>
                            <a href="<%=pathToFriendProfile%>"><%=friendName%></a>
                        </strong>
                        unlocked achievement <%=curAchievement.getAchievementType()%>
                    </p>
                </div>
            <%}%>
        <%}%>

    </div>
</div>
</body>
</html>
