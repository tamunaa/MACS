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

<form class="create-question-card"  action="addQuestions/addMultipleChoice?quizId=<%=request.getParameter("quizId")%>" method="POST" onsubmit="return collectOptionsAndSubmit();">
    <label for="questionText">Question Text:</label><br>
    <textarea id="questionText" name="questionText" rows="4" cols="50" required></textarea><br><br>

    <label>Options:</label><br>
    <div id="options-container">
        <div class="input-group">
            <input type="text" name="option[]" class="option" required>
            <button type="button" class="add-option" onclick="addOptionToDropdown(this)">Add Option</button>
            <button type="button" class="remove-option" style="display:none;" onclick="removeOption(this)">Remove Option</button>
        </div>
    </div>
    <button type="button" id="add-field">Add a Field</button>

    <br>
    <label for="answer">Correct Answer:</label><br>
    <select class="answerOptions" id="answer" name="answer" required>
        <option value="" disabled selected>Select Correct Answer</option>
    </select><br><br>

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
</form>

<script>
    function addOptionField() {
        let optionsContainer = document.getElementById("options-container");
        let newInput = document.createElement("div");
        newInput.classList.add("input-group");
        newInput.innerHTML = '<input type="text" name="option[]" class="option" required>' +
            '<button type="button" class="add-option" onclick="addOptionToDropdown(this)">Add Option</button>' +
            '<button type="button" class="remove-option" style="display:none;" onclick="removeOption(this)">Remove Option</button>';
        optionsContainer.appendChild(newInput);
    }

    document.getElementById("add-field").addEventListener("click", addOptionField);

    function addOptionToDropdown(button) {
        let inputField = button.previousElementSibling;
        let optionText = inputField.value;
        let answerDropdown = document.getElementById("answer");

        if (optionText.trim() !== "") {
            let newOption = document.createElement("option");
            newOption.value = optionText;
            newOption.text = optionText;

            let options = answerDropdown.options;
            let isDuplicate = false;
            for (let i = 0; i < options.length; i++) {
                if (options[i].value === optionText) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                answerDropdown.appendChild(newOption);
                inputField.readOnly = true; // Make the input field readonly
                button.style.display = "none"; // Hide the "Add Option" button
                button.nextElementSibling.style.display = "inline-block"; // Show the "Remove Option" button
            } else {
                alert("Option already exists. Please enter a unique option.");
            }
        }
    }

    function removeOption(button) {
        let inputField = button.previousElementSibling.previousElementSibling;
        let optionText = inputField.value;
        let answerDropdown = document.getElementById("answer");

        let options = answerDropdown.options;
        for (let i = 0; i < options.length; i++) {
            if (options[i].value === optionText) {
                answerDropdown.removeChild(options[i]);
                break;
            }
        }

        let inputGroup = button.parentElement;
        inputGroup.parentElement.removeChild(inputGroup);
    }

    function collectOptionsAndSubmit() {
        let optionsInputs = document.querySelectorAll('.option');
        let optionsContainer = document.getElementById("options");

        optionsInputs.forEach(function(input) {
            let optionValue = input.value.trim();
            if (optionValue !== "") {
                // Check if the input is inside an added input-group
                if (input.nextElementSibling.style.display === 'none') {
                    let option = document.createElement("input");
                    option.value = optionValue;
                    option.name = "options";
                    optionsContainer.appendChild(option);
                }
            }
        });

        return true;
    }

</script>

</body>
</html>
