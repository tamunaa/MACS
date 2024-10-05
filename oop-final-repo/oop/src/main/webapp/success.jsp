<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/successfullyStyle.scss">
</head>
<body>
<div class="funds-success-message-container">
    <div class="funds-checkmark-text-container">
        <div class="funds-checkmark-container">
            <svg class="funds-checkmark" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 52 52"><circle class="funds-checkmark-circle" cx="26" cy="26" r="25" fill="none"/><path class="funds-checkmark-check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/></svg>

            <div class="funds-display-on-ie">
                <svg class="funds-ie-checkmark" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 52 52"><circle class="funds-ie-checkmark-circle" cx="26" cy="26" r="25" fill="none"/><path class="funds-ie-checkmark-check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"/></svg>
            </div>
        </div>

        <h1 class="funds-success-done-text">Changed!</h1></div>

    <div class="funds-success-message">



        <h2>Your Password Or Username Changed Successfully</h2>
        <p>To Check, Please Log In.</p>
    </div>
</div>

<a href="login.jsp" style="font-size: 35px"> Log In</a>

</body>
</html>