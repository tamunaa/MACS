<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="./css/createQuestion.css">
    <meta charset="UTF-8">
    <title>Create Multiple Choice Question</title>
    <link rel="stylesheet" type="text/css" href="css/createQuestion/createMultipleChoice.css">
</head>
<body>

<form  class="create-question-card" action="addQuestions/addMultipleChoiceWithMultipleAnswer?quizId=<%=request.getParameter("quizId")%>" method="POST" onsubmit="return collectOptionsAndSubmit();">
    <label for="questionText">Question Text:</label><br>
    <textarea id="questionText" name="questionText" rows="4" cols="50" required></textarea><br><br>

    <label>Options:</label><br>
    <div id="options-container">
        <div class="input-group">
            <input type="text" name="option[]" class="option" required>
            <button type="button" class="add-option" onclick="addOption(this)">Add Option</button>
            <button type="button" class="remove-option" style="display:none;" onclick="removeOption(this)">Remove Option</button>
            <button type="button" class="mark-correct" style="display:none;" onclick="markCorrect(this)">Mark as a correct</button>
            <button type="button" class="mark-wrong" style="display:none;" onclick="markWrong(this)">Mark as a wrong</button>
        </div>
    </div>
    <button type="button" id="add-field">Add a Field</button>

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

    <div id="options" hidden="hidden"></div>
    <div id="answers" hidden="hidden"></div>
</form>

<script>
    function addOptionField() {
        let optionsContainer = document.getElementById("options-container");
        let newInput = document.createElement("div");
        newInput.classList.add("input-group");
        newInput.innerHTML = '<input type="text" name="option[]" class="option" required>' +
            '<button type="button" class="add-option" onclick="addOption(this)">Add Option</button>' +
            '<button type="button" class="remove-option" style="display:none;" onclick="removeOption(this)">Remove Option</button>' +
            '<button type="button" class="mark-correct" style="display:none;" onclick="markCorrect(this)">Mark as a correct</button>' +
            '<button type="button" class="mark-wrong" style="display:none;" onclick="markWrong(this)">Mark as a wrong</button>';
        optionsContainer.appendChild(newInput);
    }

    document.getElementById("add-field").addEventListener("click", addOptionField);

    function addOption(button) {
        let inputField = button.previousElementSibling;
        let optionText = inputField.value;

        let options = document.querySelectorAll(".option");

        if (optionText.trim() !== "") {
            let isDuplicate = false;
            for (let i = 0; i < options.length; i++) {
                // console.log()
                if (options[i].value === optionText && inputField !== options[i]) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                inputField.readOnly = true; // Make the input field readonly
                inputField.style.borderColor = "red";
                button.style.display = "none"; // Hide the "Add Option" button
                button.nextElementSibling.style.display = "inline-block"; // Show the "Remove Option" button
                button.nextElementSibling.nextElementSibling.style.display = "inline-block"; // Show the "Mark as a correct" button
                button.nextElementSibling.nextElementSibling.nextElementSibling.style.display = "none"; // Show the "Mark as a wrong" button
            } else {
                alert("Option already exists. Please enter a unique option.");
            }
        }
    }

    function markCorrect(button) {
        let inputField = button.previousElementSibling.previousElementSibling.previousElementSibling;
        inputField.style.borderColor = "green";
        button.previousElementSibling.style.display = "inline-block"; // Show the "Remove Option" button
        button.style.display = "none"; // Show the "Mark as a correct" button
        button.nextElementSibling.style.display = "Inline-block"; // Show the "Mark as a wrong" button
    }

    function markWrong(button) {
        let inputField = button.previousElementSibling.previousElementSibling.previousElementSibling.previousElementSibling;
        inputField.style.borderColor = "red";
        button.previousElementSibling.previousElementSibling.style.display = "inline-block"; // Show the "Remove Option" button
        button.previousElementSibling.style.display = "Inline-block"; // Show the "Mark as a correct" button
        button.style.display = "none"; // Show the "Mark as a wrong" button
    }


    function removeOption(button) {
        let inputGroup = button.parentElement;
        inputGroup.parentElement.removeChild(inputGroup);
    }

    function collectOptionsAndSubmit() {
        let optionsInputs = document.querySelectorAll('.option');
        let optionsContainer = document.getElementById("options");
        let answersContainer = document.getElementById("answers");
        let answerExists = false;

        optionsInputs.forEach(function(input) {
            let optionValue = input.value.trim();
            if (optionValue !== "") {
                if (input.style.borderColor === 'red' || input.style.borderColor === 'green') {
                    let option = document.createElement("input");
                    option.value = optionValue;
                    option.name = "options";
                    optionsContainer.appendChild(option);
                }
                if (input.style.borderColor === 'green') {
                    let answer = document.createElement("input");
                    answer.value = optionValue;
                    answer.name = "answers";
                    answersContainer.appendChild(answer);
                    answerExists = true;
                }
            }
        });

        if (answerExists) {
            return true;
        } else {
            alert("No answer is marked as a correct");
            return false;
        }

    }

</script>

</body>
</html>
