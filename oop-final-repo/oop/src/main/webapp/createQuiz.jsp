<%@ page import="objects.User" %>
<%@ page import="dataBase.QuizDAO" %>
<%@ page import="dataBase.UserDAO" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" type="text/css" href="css/navbar.css">
    <link rel="stylesheet" type="text/css" href="css/quiz.css">
    <script src="js/navbar.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <link rel="stylesheet" type="text/css" href="css/createQuiz.css"></head>
    <title>Create Quiz</title>
<body>
<jsp:include page="navbar.jsp" />

<%
    User currUser = ((User) request.getSession().getAttribute("currUser"));
    UserDAO userDAO = (UserDAO) request.getServletContext().getAttribute("userDAO");
    List<String> categories = userDAO.getCategories();
%>

<h1 class="text-center" >Create a New Quiz</h1>

<form action="createQuiz" method="post" class="create-quiz-form">
    <div>
        <label for="quizName">Quiz Name:</label>
        <input type="text" id="quizName" name="quizName" required>
    </div>

    <div>
        <label for="timer">Quiz Timer (in minutes):</label>
        <input type="number" id="timer" name="timer" min="0" value="0">
    </div>

    <div class="radio">
        <label for="practice">Practice Mode:</label>
        <input type="checkbox" id="practice" name="practice">
    </div>



    <div class="radio">
        <label for="category">Select Category:</label>
        <select id="category" name="category">
            <option value="" selected disabled>Select a category</option>
            <% for (String category : categories) { %>
            <option style="color: #1a1a56" value="<%= category%>">
                <%= category%>
            </option>
            <% } %>
        </select>
    </div>


    <div class="radio">
        <label for="questionDisplay">Question Display Mode:</label>
        <select id="questionDisplay" name="questionDisplay" style="margin: 3px; padding: 2px">
            <option value="singlePage">All Questions on a Single Page</option>
            <option value="onePerPage">One Question Per Page</option>
        </select>
    </div>

    <div class="radio">
<%--        <label type="hidden" for="correction">Immediate Correction:</label>--%>
        <input type="hidden" type="checkbox" id="correction" name="correction">
    </div>

    <div class="radio">
        <div><label for="randomOrder">Random Question Order:</label></div>
        <div><input type="checkbox" id="randomOrder" name="randomOrder"></div>
    </div>

    <div style="display: flex">
        <label for="quizDescription">Quiz Description:</label>
        <textarea id="quizDescription" name="quizDescription" rows="4" cols="50"></textarea>
    </div>

    <div>
        <label for="tags">Tags:</label>
        <input type="text" id="tags" name="tags" placeholder="Enter tags separated by commas">
    </div>


    <button type="submit" class="btn btn-primary">Create Quiz</button>
</form>
</body>
</html>