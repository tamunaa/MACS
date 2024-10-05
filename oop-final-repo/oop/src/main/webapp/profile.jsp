<%@ page import="objects.User" %>
<%@ page import="java.util.List" %>
<%@ page import="objects.Quiz" %>
<%@ page import="objects.History" %>
<%@ page import="dataBase.*" %>
<%@ page import="objects.Achievement" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://kit.fontawesome.com/532c01b704.js" crossorigin="anonymous"></script>
    <link rel="stylesheet" type="text/css" href="css/navbar.css">
    <script src="js/navbar.js"></script>
    <script src="js/profile.js"></script>
    <link rel="stylesheet" href="css/profile.css">

    <title>Profile</title>
</head>
<body>

<jsp:include page="navbar.jsp" />
<jsp:include page="notificationbar.jsp" />

<%
    User currUser = (User) session.getAttribute("currUser");
    boolean isAdmin = currUser.isAdmin();
    boolean isSelf = Boolean.parseBoolean(request.getParameter("self"));
    boolean areFriends = false;
    FriendsDAO friendsDAO = (FriendsDAO) request.getServletContext().getAttribute("friendsDAO");

    if (!isSelf){
        String name = request.getParameter("username");
        UserDAO dao = (UserDAO) request.getServletContext().getAttribute("userDAO");
        User newUser = dao.getUserByUsername(name);
        areFriends = friendsDAO.isFriendship(currUser, newUser);
        currUser = newUser;
    }
%>

<div class="container mt-5">
    <div class="row">
        <div class="col-md-4">
            <div class="profile--card">
                <div class="profile-header">
                    <h4 id="username"><%= currUser.getUsername()%> </h4>
                    <p><%= currUser.getEmail()%></p>

                    <%
                        if(!isSelf){
                    %>
                    <%
                        if (currUser.isAdmin()){
                    %>
                    <p id="adminButton"><i name="star" class="star bi bi-star filled"></i> admin</p>
                    <%
                    }else{
                    %>
                    <p id="adminButton"><i name="star" class="star bi bi-star"></i> admin</p>
                    <%
                        }
                    %>

                    <%
                        if (isAdmin){
                    %>
                    <script>
                        const currentUser = '<%= currUser.getUsername()%>';
                    </script>
                    <script src="js/adminStar.js"></script>
                    <%
                        }
                    %>
                    <%
                    }else if (isAdmin){
                    %>
                    <p id="adminButton"><i name="star" class="star bi bi-star filled"></i> admin</p>
                    <%
                    }else{
                    %>
                    <p id="adminButton"><i name="star" class="star bi bi-star"></i> admin</p>
                    <%
                        }
                    %>

                    <%if(isSelf){%>
                    <div>
                        <button type="button" id="toggle-username-section" class="change-button">
                            <i class="bi bi-pencil"></i> Change UserName</button>
                        <button type="button" id="toggle-password-section" class="change-button">
                            <i class="bi bi-pencil"></i> Change Password</button>
                    </div>

                    <div id="change-username-section">
                        <form action="ChangeUsernameServlet" method="post">
                            <label for="password">New Username:</label>
                            <input type="text" id="newUsername" name="newUsername" class="form-control" required>
                            <br>
                            <button type="submit" class="btn btn-primary btn-sm">Change</button>
                        </form>
                    </div>

                    <div id="change-password-section">
                        <form action="ChangePasswordServlet" method="post">
                            <label for="password">New Password:</label>
                            <input type="password" id="password" name="password" class="form-control" required>
                            <br>
                            <button type="submit" class="btn btn-primary btn-sm">Change</button>
                        </form>
                    </div>

                    <script>
                        const passwordSection = document.getElementById('change-password-section');
                        const toggleButton = document.getElementById('toggle-password-section');

                        const userNameSection = document.getElementById('change-username-section');
                        const userNameButton = document.getElementById('toggle-username-section');

                        passwordSection.style.display = 'none';

                        toggleButton.addEventListener('click', function() {
                            if (passwordSection.style.display === 'none') {
                                passwordSection.style.display = 'block';
                            } else {
                                passwordSection.style.display = 'none';
                            }
                        });

                        userNameSection.style.display = 'none';
                        userNameButton.addEventListener('click', function() {
                            if (userNameSection.style.display === 'none') {
                                userNameSection.style.display = 'block';
                            } else {
                                userNameSection.style.display = 'none';
                            }
                        });
                    </script>

                    <%}%>
                </div>

                <div class="profile-info">
                    <p><strong>Registration Date:</strong> <%= currUser.getDate()%></p>
                </div>
                <div class="profile-actions" id="actions">

                    <%
                        String url = "/SendFriendRequestServlet";

                        request.getSession().setAttribute("username", currUser.getUsername());
                    %>
                    <%if(!areFriends){%>
                    <form action='<%=url%>' method="GET">
                        <button class="custom-btn2">
                            <i class="bi bi-person-plus"></i>  send friend request
                        </button>
                    </form>
                    <%}%>

                    <% if (areFriends) { %>
                        <a class="custom-btn2" href="messages.jsp?recipient=<%=currUser.getUsername()%>">
                            <i class="bi bi-envelope-at-fill"></i> Send Message
                        </a>
                    <% } %>

                </div>
            </div>


            <%List<User> friends = ((FriendsDAO)request.getServletContext().getAttribute("friendsDAO")).getAllFriends(currUser);%>
            <div class="friends-list" style="height: 250px; overflow-y: auto;">
                <h4>Friends</h4>
                <%for(int i = 0; i < friends.size(); i++){
                    String friendName = friends.get(i).getUsername();
                    String path = "profile.jsp?self=false&&username="+friendName;
                %>
                <div class="friend-item" style="display: flex; justify-content: space-between">
                    <div><a class="friend-name" href= "<%=path%>"><%=friendName%></a></div>
                    <%if (isSelf){%>
                    <form action="RemoveFriendServlet" method="GET">
                        <button>
                            <%request.getSession().setAttribute("username", friendName);%>
                            <i class="fas fa-trash-alt"></i>
                        </button>
                    </form>
                    <%}%>
                </div>
                <%}%>
            </div>

            <div class="grade-icon">
                <form action="GradingServlet" method="GET">
                    <button type="submit" class="btn btn-secondary">
                        <i class="bi bi-list-check"></i>GRADE
                    </button>
                </form>
            </div>


        </div>

        <div class="col-md-8">
            <!-- Taken Quizzes -->
            <%
                HistoryDAO historyDAO =((HistoryDAO)request.getServletContext().getAttribute("historyDAO"));
                List<History> takenQuizzes = historyDAO.getHistoryByUserId(currUser.getId());
                QuizDAO quizDAO = ((QuizDAO)request.getServletContext().getAttribute("quizDAO"));
            %>

            <div class="profile-section">
                <div class="profile-card">
                    <h2>Taken Quizzes</h2>
                    <ul class="custom-link-list">
                        <%for (int i = 0; i < takenQuizzes.size(); i++){
                            History curQuizHistory = takenQuizzes.get(i);
                            String quizName = quizDAO.getQuizByID(curQuizHistory.getQuizId()).getQuizName();
                            String pathForQuizPage = "quizPage.jsp?searchInput="+quizName;
                        %>
                        <li><a href="<%=pathForQuizPage%>"> <%=quizName%></a></li>
                        <%}%>
                    </ul>
                </div>
            </div>

            <!-- Created Quizzes -->
            <%
                List<Quiz> createdQuizzes = quizDAO.getQuizzesByAuthor(currUser.getId());

            %>
            <div class="profile-section">
                <div class="profile-card">
                    <h2>Created Quizzes</h2>
                    <ul class="custom-link-list">
                        <%for(int i = 0; i < createdQuizzes.size(); i++){
                            Quiz curQuiz = createdQuizzes.get(i);
                            String quizPagePath = "quizPage.jsp?searchInput="+curQuiz.getQuizName();
                        %>
                        <li><a href="<%=quizPagePath%>"> <%=curQuiz.getQuizName()%> </a></li>
                        <%}%>
                    </ul>
                </div>
            </div>

            <!-- Achievements -->
            <%
                AchievementDAO  AchievementsDAO = (AchievementDAO)request.getServletContext().getAttribute("achievementDAO");
                List<Achievement> achievements = AchievementsDAO.getUserAchievements(currUser.getId());
            %>

            <div class="profile-section">
                <div class="profile-card" >
                    <h2>Achievements</h2>
                    <div class="custom-link-list" id ="achievements">
                        <%for (int i = 0; i < achievements.size(); i++){
                        Achievement curAchievement = achievements.get(i);
                        String name = curAchievement.getAchievementType();
                        %>
                        <div><img src="<%=curAchievement.getPictureUrl()%>"  width="100px" height="140" alt="amateur"></div>

                        <%
                        }
                        %>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>


<script>
    const actions = document.getElementById('actions')
    if (<%= isSelf %>) {
        actions.style.display = 'none';
    } else {
        actions.style.display = 'block';
    }
</script>

</body>
</html>

