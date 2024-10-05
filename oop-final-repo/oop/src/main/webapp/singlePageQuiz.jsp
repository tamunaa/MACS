<%@ page import="objects.questions.Question" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Question[] questions = (Question[]) request.getSession().getAttribute("questions");
%>
<html>
<head>

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>


    <%
        String cssFolderPath = "questions/css/"; // Path to your CSS folder
        String[] cssFiles = new java.io.File(application.getRealPath(cssFolderPath)).list();
        if (cssFiles != null) {
            for (String cssFile : cssFiles) {
                if (cssFile.endsWith(".css")) {
    %>
    <link rel="stylesheet" type="text/css" href="<%= cssFolderPath + cssFile %>">
    <%
                }
            }
        }
    %>

    <link rel="stylesheet" type="text/css" href="questions/css/base.css">
    <link rel="stylesheet" type="text/css" href="questions/css/Matching.css">
    <%--    <link rel="stylesheet" type="text/css" href="questions/css/Multi.css">--%>

    <title>quiz</title>
</head>
<body>

<form action="quizSummary?quizId=<%=request.getParameter("quizId")%>" method="POST">
    <div class="question-page">
        <div>
            <%
                for (int i = 0; i < questions.length; i++) {
            %>
            <input type="text" name="answers" id="answer<%=i%>" hidden="hidden">
            <%
                }
            %>
        </div>
            <%
            int i = 0;
            while (i < questions.length) {
                Question current = questions[i];
                request.setAttribute("index", i);
                request.setAttribute("current", current);
                String type = current.getQuestionType();
                String path = "questions/"+type+".jsp";
        %>
        <div class="question-card">
            <jsp:include page="<%= path %>" />

        </div>
            <%
            i++;
            }
        %>
        <button class="finish">submit</button>
</form>
</div>
<%--    <script src="questions/js/Matching.js"></script>--%>
<script>
    function saveAnswersForOneInputQuestions(index) {
        let inputField = document.getElementsByName("question" + index);
        let value = inputField[0].value;
        let answers = document.getElementById("answer" + index);
        answers.value = value;
    }
    function getMatchingAnswers(index, total) {
        let value = '';
        for (let i = 0; i < total; i++) {
            let left = document.getElementById("question" + index + "left" + i);
            let right = document.getElementById("question" + index + "right" + i);
            value += left.textContent + ':' + right.textContent;
            if (i + 1 != total) {
                value += ',';
            }
            let answers = document.getElementById("answer" + index);
            answers.value = value;
        }
    }

    function saveMultipleChoiceWithMultipleAnswerAnswers(index) {
        let checkBoxButtons = document.querySelectorAll("input[type=\"checkbox\"][name=\"question" + index + "\"]");
        let value = '';
        for (let i = 0; i < checkBoxButtons.length; i++) {
            if (checkBoxButtons[i].checked) {
                value += checkBoxButtons[i].value + ',';
            }
        }
        value = value.substring(0, value.length - 1);
        let answers = document.getElementById("answer" + index);
        answers.value = value;
    }

    function saveMultipleChoiceAnswer(index) {
        let radioButtons = document.querySelectorAll('.question' + index);
        let value = '';
        for (let i = 0; i < radioButtons.length; i++) {
            if (radioButtons[i].checked) {
                value = radioButtons[i].value;
                break;
            }
        }
        let answers = document.getElementById("answer" + index);
        answers.value = value;
    }

    function saveMultiAnswerAnswers(index, numFields) {
        let value = '';
        for (let i = 0; i < numFields; i++) {
            let inputField = document.getElementById("question" + index + i);
            value += inputField.value;
            if (i + 1 != numFields) {
                value += ',';
            }
        }
        let answers = document.getElementById("answer" + index);
        answers.value = value;
    }

</script>
</body>
</html>
