<%@ page import="objects.questions.Question" %>
<%
    Question question = (Question)request.getAttribute("current");
    String indexStr = request.getAttribute("index").toString();
    int numFields = question.getNumFields();
    int index = Integer.parseInt(indexStr);
%>
<h3>Multi Answer</h3>
<div class="question">
    <h2> <%=question.getQuestion()%> </h2>
</div>

<div class="user-answer">
    <label>Your Answer:</label>
    <%
        for (int i = 0; i < numFields; i++) {
    %>
        <input type="text" id="question<%=index%><%=i%>" oninput="saveMultiAnswerAnswers(<%=index%>, <%=numFields%>)">
    <%
        }
    %>
</div>
