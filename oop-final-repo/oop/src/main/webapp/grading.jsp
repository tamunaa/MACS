<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="objects.QuestionResponsePair" %>
<%@ page import="objects.Response" %>
<%@ page import="com.google.gson.Gson" %>

<!DOCTYPE html>
<html>
<%
    // Retrieve the questionResponse list from session or create it if not present
    List<QuestionResponsePair> questionResponsePairs = (ArrayList<QuestionResponsePair>)request.getSession().getAttribute("responses");
%>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

    <script src="js/navbar.js"></script>
    <link rel="stylesheet" type="text/css" href="css/grading.css">
    <meta charset="UTF-8">
    <title>Grade Questions</title>
<script>
    var questionResponsePairs = <%= new Gson().toJson(questionResponsePairs) %>;

    function saveScore(responseIndex) {
        console.log("saveScore() called for response index:", responseIndex);

        var scoreInput = document.getElementById('score_' + responseIndex);
        var score = scoreInput.value;

        // Update the grade for the response in questionResponsePairs
        questionResponsePairs[responseIndex].response.grade = parseInt(score);
        questionResponsePairs[responseIndex].response.isGraded = true;

        // Update the session attribute using AJAX
        var xhr = new XMLHttpRequest();
        xhr.open("POST", "UpdateSessionAttributeServlet", true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

        var data = "data=" + encodeURIComponent(JSON.stringify(questionResponsePairs));

        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    console.log("Session attribute updated on the server:", xhr.responseText);
                } else {
                    console.error("Failed to update session attribute:", xhr.status, xhr.statusText);
                }
            }
        };

        xhr.send(data);
    }
</script>




</head>
<body>
   <jsp:include page="navbar.jsp" />
        <jsp:include page="notificationbar.jsp" />
    <h1>Grade Questions</h1>
    <table border="1">
        <tr>
            <th>Question</th>
            <th>Response</th>
            <th>Score</th>
        </tr>
        <% for (int i = 0; i < questionResponsePairs.size(); i++) { %>
        <tr>
            <td><%= questionResponsePairs.get(i).getQuestionText() %></td>
            <td><%= questionResponsePairs.get(i).getResponse().getResponseText() %></td>
            <td>
                <input type="number" id="score_<%= i %>" name="score_<%= i %>" min="0" max="10" required
                       oninput="saveScore(<%= i %>)" value="<%= questionResponsePairs.get(i).getResponse().getGrade() %>">
            </td>
        </tr>
        <% } %>
    </table>
    <form action="GradingServlet" method="POST">
        <input type="submit" value="gradingFinished">
    </form>
</body>
</html>
