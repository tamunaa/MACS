<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="./css/createQuestion.css">
    <meta charset="UTF-8">
    <title>Create Picture Response</title>
    <link rel="stylesheet" type="text/css" href="css/createQuestion/createPictureResponse.css">
</head>
<body>

<form  class="create-question-card" action="addQuestions/addPictureResponse?quizId=<%=request.getParameter("quizId")%>" method="POST">
    <label for="questionText">Question Text:</label><br>
    <textarea id="questionText" name="questionText" rows="4" cols="50" required></textarea><br><br>

    <label for="pictureURL">Picture URL:</label><br>
    <input type="text" id="pictureURL" name="pictureURL" required><br><br>

    <label for="answer">Answer:</label><br>
    <input type="text" id="answer" name="answer" required><br><br>

    <%
        boolean timerIsAllowed = Boolean.parseBoolean(request.getAttribute("timerIsAllowed").toString());
    %>

    <%
        if (!timerIsAllowed){
    %>
    <p>Timed questions are only allowed in one-question-per-page format quizzes</p>
    <%
        }
    %>
    <label for="timer">Timer (in seconds):</label><br>
    <input type="number" id="timer" name="timer" min="1" <%if(!timerIsAllowed) out.print("readonly");%>><br><br>


    <!-- Buttons -->
    <button type="submit">Add Picture</button>
    <a href="/editQuiz?quizId=<%=request.getParameter("quizId")%>"><button type="button">Cancel</button></a>
</form>

</body>
</html>
