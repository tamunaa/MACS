<%--
  Created by IntelliJ IDEA.
  User: mariamtsikarishvili
  Date: 22.08.23
  Time: 13:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="./css/createQuestion.css">
    <meta charset="UTF-8">
    <title>Create Graded</title>
    <link rel="stylesheet" type="text/css" href="css/createQuestion/createQuestionResponse.css"></head>
</head>
<body>

<form class="create-question-card"  action="addQuestions/addGraded?quizId=<%=request.getParameter("quizId")%>" method="POST">
    <label for="questionText">Question Text:</label><br>
    <textarea id="questionText" name="questionText" rows="4" cols="50" required></textarea><br><br>

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
    <button type="submit">Add Question</button>
    <a href="/editQuiz?quizId=<%=request.getParameter("quizId")%>"><button type="button">Cancel</button></a>
</form>

</body>
</html>

