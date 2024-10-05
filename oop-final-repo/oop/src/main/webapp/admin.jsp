<%@ page import="objects.User" %>
<%@ page import="java.util.List" %>
<%@ page import="dataBase.UserDAO" %>
<%@ page import="dataBase.QuizDAO" %>
<%@ page import="objects.Quiz" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://kit.fontawesome.com/532c01b704.js" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="css/admin.css">

    <title>Admin Area</title>

</head>
<body>
<header class="text-center py-4">
    <p class="text-center">Admin Area</p>
    <form action="NewsFeedServlet" method="get">
        <button type="submit" class="go-back">
            <a class="text-center" href="home.jsp">
                <i class="fa-solid fa-person-walking-arrow-loop-left"></i>
            </a>
        </button>
    </form>
</header>

<div class="admin-section scrollable">

    <%
        List<User> userList = ((UserDAO)request.getServletContext().getAttribute("userDAO")).getAllUsers();
        List<Quiz> quizList = ((QuizDAO)request.getServletContext().getAttribute("quizDAO")).getAllQuizzes();
    %>

    <h2>Manage Users</h2>
    <div>
        <table class="quizzes-table">
            <tbody>
            <%
                for(int i = 0; i < userList.size(); i++){
                    String curUserName =userList.get(i).getUsername();
                    String pathToUser = "profile.jsp?self=false&&username="+curUserName;
            %>
            <tr>
                <td>
                    <div class="d-flex justify-content-between align-items-center">
                        <a class="me-3" href= "<%=pathToUser%>" ><%=curUserName%></a>
                        <form action="AdminServlet" method="GET">
                            <label for="deleteUser">
                                <input type="hidden" id="deleteUser" name="deleteUser" value="<%=curUserName%>">
                            </label>
                            <input type="hidden" name="type" value="user">

                            <button class="trash">
                                <i class="fas fa-trash-alt"></i>
                            </button>
                        </form>
                    </div>
                </td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</div>


<div class="admin-section scrollable">
    <h2>Manage Quizzes</h2>
    <div>
        <table class="quizzes-table">
            <tbody>
                        <%
                            for(int i = 0; i < quizList.size(); i++){
                                String curQuizName =quizList.get(i).getQuizName();
                                String pathToQuiz = "quizPage.jsp?searchInput="+curQuizName;
                        %>
                        <tr>
                            <td>
                                <div class="d-flex justify-content-between align-items-center">
                                    <a class="me-3" href="<%=pathToQuiz%>"><%=curQuizName%></a>

                                    <form action="AdminServlet" method="GET">
                                        <input type="hidden" name="deleteQuiz" value="<%=curQuizName%>">
                                        <input type="hidden" name="type" value="quiz">
                                        <button class="trash"><i class="fas fa-trash-alt"></i></button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                        <%
                            }
                        %>
            </tbody>
        </table>
    </div>
</div>


<section class="admin-section p-4">
    <h2 class="mb-4">Make Announcements</h2>
    <form id="announcement-form" action="AdminServlet" method="POST">
        <label for="announcementTitle">Title:</label>
        <input id="announcementTitle" name="announcementTitle" class="form-control mb-1" required>

        <label for="announcementText">Announcement:</label>
        <textarea id="announcementText" name="announcementText"  class="form-control mb-3" required></textarea>
        <button type="submit" class="btn btn-primary">Make Announcement</button>
    </form>
</section>


<section class="admin-section p-4">
    <h2 class="mb-4">Add Category</h2>
    <form id="add-category-form" action="AdminServlet" method="GET">
        <input type="hidden" name="type" value="addCategory">

        <label for="categoryName">Category Name:</label>
        <input id="categoryName" name="categoryName" class="form-control mb-1" required>

        <button type="submit" class="btn btn-primary">Add Category</button>
    </form>
</section>


</body>
</html>
