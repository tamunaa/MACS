<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="objects.questions.Question" %>
<%
    Question[] questions = (Question[]) request.getSession().getAttribute("questions");
%>
<html>
<head>
    <title>Title</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link rel="stylesheet" type="text/css" href="questions/css/Matching.css">
    <link rel="stylesheet" type="text/css" href="questions/css/multi.css">

    <style>
        body {
            flex-direction: column;
        }
    </style>
</head>
<body>

<div class="question-space">
    <div class="question-card" id="dynamic-container">
        <h1 style="font-size: 50px">Good Luck</h1>
    </div>
</div>
<form action="quizSummary?quizId=<%=request.getParameter("quizId")%>" method="POST" onsubmit="return checkIfFinished();">
    <div>
        <%
            for (int i = 0; i < questions.length; i++) {
        %>
        <input type="text" name="answers" id="answer<%=i%>" hidden="hidden">
        <%
            }
        %>
    </div>
    <button type="submit" class="finish" id="load-button" onclick="nextAction()">Next</button>
</form>


<script>
    let currentIndex = 0;
    let total = <%=questions.length%>;

    const dynamicContainer = $("#dynamic-container");
    const loadButton = $("#load-button");

    function checkIfFinished() {
        return currentIndex > total;
    }

    function nextAction() {
        if (currentIndex < total) {
            loadDynamicContent(currentIndex);
        }
        if (currentIndex + 1 === total) {
            loadButton.text("Finish Quiz");
        }
        currentIndex++;
        return checkIfFinished();
    }

    function loadDynamicContent(index) {
        const path = "loadQuestions.jsp";
        dynamicContainer.fadeOut(300, function() {
            $.ajax({
                url: path,
                type: "POST",

                data: { index: index},
                success: function(data) {
                    dynamicContainer.html(data);
                    dynamicContainer.fadeIn(300);
                }
            });
        });
    }

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
