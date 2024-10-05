<%@ page import="objects.questions.Question" %>
<%
    Question question = (Question)request.getAttribute("current");
    String indexStr = request.getAttribute("index").toString();
    int index = Integer.parseInt(indexStr);
%>
<h3>Multiple Choice Question</h3>
<div class="question"><h2><%=question.getQuestion()%></h2></div>
<div class="user-answer">
    <%
        for (String option : question.getOptions()) {
    %>
        <input type="radio" class="question<%=index%>" name="question<%=index%>" value="<%=option%>" onclick="saveMultipleChoiceAnswer(<%=index%>)"><%=option%>
        <br>
    <%
    }
    %>
</div>