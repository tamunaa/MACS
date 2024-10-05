<%@ page import="objects.questions.Question" %>

<%
    Question[] questions = (Question[]) request.getSession().getAttribute("questions");
    int total = questions.length;
    int index = Integer.parseInt(request.getParameter("index"));
    request.setAttribute("index", index);
    if (index>=total){
        index%=total;
    }
    out.print("<h1> Question "+(index+1) +"/" + total +"</h1>");

    String type = questions[index].getQuestionType();
    String path = "questions/" + type + ".jsp";
    request.setAttribute("current", questions[index]);
%>
<jsp:include page="<%= path %>" />
