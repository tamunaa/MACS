<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="./css/createQuestion.css">
    <meta charset="UTF-8">
    <title>Create Multiple Choice Question</title>
    <link rel="stylesheet" type="text/css" href="css/createQuestion/createMultipleChoice.css">
    <link rel="stylesheet" type="text/css" href="css/createQuestion/createMatching.css">
</head>
<body>

<form  class="create-question-card" action="addQuestions/addMatching?quizId=<%=request.getParameter("quizId")%>" method="POST" onsubmit="return checkState();">
    <label for="questionText">Question Text:</label><br>
    <textarea id="questionText" name="questionText" rows="4" cols="50" required></textarea><br><br>

    <div id="options-container">
        <p class="left">LEFT</p>
        <p class="right">RIGHT</p>
        <div class="input-group">
            <input type="text" name="left" class="left" required>
            <button type="button" class="remove-option" onclick="removeOption(this)">X</button>
            <input type="text" name="right" class="right" required>
        </div>
        <div class="input-group">
            <input type="text" name="left" class="left" required>
            <button type="button" class="remove-option" onclick="removeOption(this)">X</button>
            <input type="text" name="right" class="right" required>
        </div>
    </div>
    <button type="button" id="add-field">Add a Pair</button>

    <br>

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
    function addOptionField() {
        let optionsContainer = document.getElementById("options-container");
        let newInput = document.createElement("div");
        newInput.classList.add("input-group");
        newInput.innerHTML = '<input type="text" name="left" class="left" required>' +
        '<button type="button" class="remove-option" onclick="removeOption(this)">X</button>' +
        '<input type="text" name="right" class="right" required>';
        optionsContainer.appendChild(newInput);
        checkState();
    }

    document.getElementById("add-field").addEventListener("click", addOptionField);


    function removeOption(button) {
        let inputGroup = button.parentElement;
        inputGroup.parentElement.removeChild(inputGroup);
    }

    function checkState() {
        let pairs = document.querySelectorAll(".left");
        console.log(pairs.length);
        if (pairs.length < 3) {
            alert("At least 2 pairs is allowed.")
            return false;
        }
        return true;
    }

</script>


</body>
</html>
