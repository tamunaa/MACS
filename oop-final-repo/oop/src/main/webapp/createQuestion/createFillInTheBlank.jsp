<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <link rel="stylesheet" type="text/css" href="./css/createQuestion.css">

  <meta charset="UTF-8">
  <link rel="stylesheet" type="text/css" href="./css/createQuestion/createFillInTheBlank.css">
  <script>
    let blankSpaceAdded = false;

    function addBlankSpace() {
      if (!blankSpaceAdded) {
        let textarea = document.getElementById('questionText');
        textarea.value += " __________ ";
        blankSpaceAdded = true;
      }
    }

    function removeBlankSpace() {
      let textarea = document.getElementById('questionText');
      textarea.value = textarea.value.replaceAll("_", "");
      blankSpaceAdded = false;
    }

    function checkBlankSpace() {
      let textarea = document.getElementById('questionText');
      let text = textarea.value;
      let regex = /_+/g;
      let matches = text.match(regex);

      if (matches && matches.length === 1) {
        return true;
      } else {
        alert("Please use exactly one blank space (____) in the question text.");
        return false;
      }
    }
  </script>
</head>

<body>
<form class="create-question-card" action="addQuestions/addFillInTheBlank?quizId=<%=request.getParameter("quizId")%>" method="POST" onsubmit="return checkBlankSpace();">
  <label for="questionText">Question Text:</label><br>
  <textarea id="questionText" name="questionText" rows="4" cols="50" required></textarea><br><br>

  <button type="button" onclick="addBlankSpace()">Add a Blank Space</button>

  <button type="button" onclick="removeBlankSpace()">Remove a Blank Spaces</button>

  <br><br>
  <label for="answer">Answer:</label><br>
  <input type="text" id="answer" name="answer" required><br><br>

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

</body>
</html>
