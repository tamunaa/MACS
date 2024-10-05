<%@ page import="objects.Quiz" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="objects.History" %>
<%@ page import="java.util.ArrayList" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" type="text/css" href="css/navbar.css">
    <script src="js/navbar.js"></script>
    <link rel="stylesheet" type="text/css" href="css/quiz.css">
    <title>Quiz</title>
</head>
<body>

<%
    Quiz quiz = (Quiz) session.getAttribute("quiz");
    String author = (String) request.getAttribute("author");
    List<History> historyList = (ArrayList<History>) request.getAttribute("historyList");
    List<String> historyUsernames = (ArrayList<String>) request.getAttribute("historyUsernames");
%>


<jsp:include page="navbar.jsp" />
<jsp:include page="notificationbar.jsp" />

<div class="quiz-container">
    <div class="quiz-info">
        <h1><%=quiz.getQuizName()%></h1>
        <p>author: <a href="profile.jsp?user=<%=author%>"><%=author%></a></p>
        <p>Time for Quiz: <%=quiz.getTimer()%> minutes</p>
        <p>Created Date: <%=new SimpleDateFormat("dd MMMM, yyyy HH:mm").format(quiz.getDateCreated())%></p>
        <p>Description: <%=quiz.getDescription()%></p>
    </div>
    <div class="quiz-options">
        <%
            if (quiz.isPractice()) {
                out.println("<a href=\"#\" class=\"practice-btn\">Practice</a>");
            }
        %>
        <a href="question" class="take-btn">Take Quiz</a>

    </div>

    <div class="quiz-history">
        <h2>Quiz History</h2>
        <table>
            <tr>
                <th>When</th>
                <th>Who</th>
                <th>Time</th>
                <th>Score</th>
            </tr>
            <%
                for (int i = 0; i < historyList.size(); i++) {
                    String date = new SimpleDateFormat("dd MMMM, yyyy HH:mm").format(historyList.get(i).getDateTaken());
                    int timeElapsed = (int)historyList.get(i).getTimeRelapsed();
            %>
                <tr>
                    <td><%=date%></td>
                    <td><a href="profile.jsp?user=<%=historyUsernames.get(i)%>"><%=historyUsernames.get(i)%></a></td>
                    <td><%=String.format("%02d:%02d", timeElapsed / 60, timeElapsed % 60)%></td>
                    <td><%=historyList.get(i).getScore()%></td>
                </tr>
            <%
                }
            %>
        </table>
    </div>

</div>
</body>
</html>
