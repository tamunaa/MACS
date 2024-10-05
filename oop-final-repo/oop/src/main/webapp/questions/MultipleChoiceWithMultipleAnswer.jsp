<%@ page import="objects.questions.Question" %>
<%
    Question question = (Question)request.getAttribute("current");
    String indexStr = request.getAttribute("index").toString();
    int index = Integer.parseInt(indexStr);
%>
<h3>Multiple Choice With Multiple Answers</h3>
<div class="question"><h2>Please mark each statement below which is true:</h2></div>
<div class="user-answer">
    <%
        for (String option : question.getOptions()) {
    %>
        <input type="checkbox" name="question<%=index%>" value="<%=option%>" onclick="saveMultipleChoiceWithMultipleAnswerAnswers(<%=index%>)"><%=option%>
        <br>
    <%
        }
    %>
</div>