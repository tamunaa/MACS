<%--
  Created by IntelliJ IDEA.
  User: mariamtsikarishvili
  Date: 22.08.23
  Time: 13:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="./css/createQuestion.css">
    <meta charset="UTF-8">
    <title>Create MultiAnswer</title>
    <link rel="stylesheet" type="text/css" href="css/createQuestion/createQuestionResponse.css"></head>
</head>
<body>

<form class="create-question-card"  action="addQuestions/addMultiAnswer?quizId=<%=request.getParameter("quizId")%>" method="POST" onsubmit="return validate();">
    <label for="questionText">Question Text:</label><br>
    <textarea id="questionText" name="questionText" rows="4" cols="50" required></textarea><br><br>

    <label >How many answers should the quiz taker provide?</label><br>
    <input type="number" id="numAnswers" name="numAnswers" value="1" min="1" onclick="addFields()" required>
    <button type="button" onclick="addFields()">Add answers</button>
    <br><br>

    <div id="answers-container">
        <label for="answer0">Answer 1:</label><br>
        <input type="text" class="answers" id="answer0" name="answers" required>
    </div>

    <div class="ordering">
        <div class="ordered">
            <input type="radio" id="ordered" name="ordering" value="ordered">
            <label for="ordered">Ordered</label>
        </div>
        <div class="notOrdered">
            <input type="radio" id="notOrdered" name="ordering" value="notOrdered" checked>
            <label for="notOrdered">Not Ordered</label>
        </div>
    </div>


    <%
        boolean timerIsAllowed = Boolean.parseBoolean(request.getAttribute("timerIsAllowed").toString());
    %>

    <%
        if (!timerIsAllowed){
    %>
    <p>Timed questions are only allowed in one-question-per-page format quizzes</p>
    <%
        }
    %>
    <label for="timer">Timer (in seconds):</label><br>
    <input type="number" id="timer" name="timer" min="1" <%if(!timerIsAllowed) out.print("readonly");%>><br><br>

    <!-- Buttons -->
    <button type="submit">Add Question</button>
    <a href="/editQuiz?quizId=<%=request.getParameter("quizId")%>"><button type="button">Cancel</button></a>
</form>

<script>
    let numFields = 1;

    function addFields() {
        let numAnswers = document.getElementById("numAnswers").value;
        let answersContainer = document.getElementById("answers-container");

        if (numAnswers < 1) {
            alert("Quantity must be a positive number");
        } else {
            // For removing
            for (numFields; numAnswers < numFields; numFields--) {
                let obj = document.getElementById("answer" + (numFields - 1));
                let inputGroup = obj.parentElement;
                inputGroup.parentElement.removeChild(inputGroup);
            }

            // For adding
            for (numFields; numFields < numAnswers; numFields++) {
                let newInput = document.createElement("div");
                newInput.classList.add("input-group");
                newInput.innerHTML = '<label for="answer' + numFields + '">Answer ' + (numFields + 1) + ':</label><br>' +
                    '<input type="text" class="answers" id="answer' + numFields + '" name="answers" required>';
                answersContainer.appendChild(newInput);
            }

        }
    }

    function validate() {
        let answers = document.querySelectorAll(".answers");
        let uniqueArray = [];
        if (answers.length < 1) {
            alert("Quantity must be a positive number");
            return false;
        } else {
            for (let i = 0; i < answers.length; i++) {
                if (!uniqueArray.includes(answers[i].value)) {
                    uniqueArray.push(answers[i].value);
                } else {
                    alert("All answers must be unique");
                    return false;
                }
            }
        }
        return true;
    }
</script>
<style>
    .ordering {
        font-family: Arial, sans-serif;
        /*margin: 10px;*/
        margin-bottom: 15px;
        margin-top: 15px;
        display: flex;
        flex-direction: column;
    }

    .ordering input[type="radio"] {
        margin-bottom: 10px;
    }

    .ordering label {
        cursor: pointer;
    }

</style>
</body>
</html>

