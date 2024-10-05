<%@ page import="objects.questions.Question" %>
<%
    Question question = (Question)request.getAttribute("current");
    String indexStr = request.getAttribute("index").toString();
    int index = Integer.parseInt(indexStr);
%>
<h3>Graded Question</h3>

<div class="question">
    <h2><%= question.getQuestion() %></h2>
</div>

<div class="user-answer">
    <label>Your Answer:</label>
    <textarea type="text" name="question<%=index%>" required rows="5" cols="50" oninput="saveAnswersForOneInputQuestions(<%=index%>)"></textarea>
</div>

