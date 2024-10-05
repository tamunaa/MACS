<%@ page import="objects.Quiz" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="objects.questions.Question" %>
<%@ page import="objects.History" %>
<%@ page import="objects.User" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Objects" %>
<%@ page import="dataBase.questionsDAOs.ResponseDAO" %>
<%@ page import="objects.Review" %>
<%@ page import="dataBase.*" %>
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
  <link rel="stylesheet" type="text/css" href="css/quizResult.css">

  <title>Quiz Result</title>
</head>
<body>
<jsp:include page="navbar.jsp" />
<jsp:include page="notificationbar.jsp" />

<%
  QuizDAO quizDAO = (QuizDAO) request.getServletContext().getAttribute("quizDAO");
  Quiz currQuiz = (Quiz) quizDAO.getQuizByID(Integer.parseInt(request.getParameter("quizId")));
  User currUser = (User) request.getSession().getAttribute("currUser");
  HistoryDAO historyDAO = (HistoryDAO) request.getServletContext().getAttribute("historyDAO");

  int quizId = Integer.parseInt(request.getParameter("quizId"));
  Question[] questions = (Question[]) request.getSession().getAttribute("questions");
  String[] answers = request.getParameterValues("answers");

  int score = 0;
  int max_score = 0;

%>
<div class="result">

<%for (int i = 0; i < answers.length; i++){%>

<%if(!Objects.equals(questions[i].getQuestionType(), "Graded")){%>


<div class="review-container" >
  <label><%= questions[i].getQuestion()%></label>
  <p>
    Correct Answer : <%= Arrays.toString(questions[i].getCorrectAnswers())%>
    <br> Your Answer: <%= answers[i]%>
    <%
      int curScore = questions[i].evaluate(answers[i]);
      int cur_max_score = questions[i].getCorrectAnswers().length;
      score += curScore;
      max_score += cur_max_score;
    %>
    <br>Score: <%=curScore%> / <%=cur_max_score%>
  </p>
</div>
<%  }
} %>

<div class="review-section">
  Overall Score: <%=score%> / <%=max_score%>
  <br>
  <%long time = ((((Timestamp)request.getSession().getAttribute("endTime")).getTime()-
          ((Timestamp) request.getSession().getAttribute("startTime")).getTime()) / 1000) % 60;%>
  Time: <%=time%>seconds
  <br>
<%--  <%if("false".equals(request.getAttribute("practice").toString())){%>--%>
  <% History history = new History(currQuiz.getID(), currUser.getId(), score, time, (Timestamp)request.getSession().getAttribute("endTime"));
    historyDAO.insertHistory(history);
    for (int i = 0; i < questions.length; i++) {
      if (Objects.equals(questions[i].getQuestionType(), "Graded")) {
        ResponseDAO responseDAO = (ResponseDAO) request.getServletContext().getAttribute("responseDAO");
        responseDAO.addResponse(questions[i].getQuestionId(), history.getId(), score, false, answers[i]);
      }
    }
  %>
<%--  <%}%>--%>
</div>

<div class="review-section">
  <form action="review?quizId=<%=request.getParameter("quizId")%>" method="post">
    Review:  <input type="text" name="review">
    <br>
    <label for="rating">Rate Quiz between 1 and 10</label>
    <input type="number" min="1" max="10" name="rating" id="rating">
    <br>
    <button type="submit" class="btn btn-secondary">Add Rating and Review</button>
  </form>
</div>
</div>

</body>
</html>