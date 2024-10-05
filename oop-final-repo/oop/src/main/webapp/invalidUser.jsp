<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>New Title</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/newStyles.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f0f0f0;
        }
        .header {
            background-color: #333;
            color: white;
            text-align: center;
            padding: 10px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff;
            border: 1px solid #ddd;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .error-message {
            font-size: 24px;
            text-align: center;
            margin: 20px;
        }
        .error-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            text-decoration: none;
            color: #333;
            background-color: #f0f0f0;
            padding: 10px 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
    </style>
</head>
<body>
<div class="header">
    <h1>New Title</h1>
</div>
<div class="container">
    <section class="error">
        <!-- Content -->
        <div class="error-message">
            <%=request.getAttribute("mess")%>
        </div>
        <div class="error-link">
            <a href="${pageContext.request.contextPath}/login.jsp">Go to Homepage</a>
        </div>
    </section>
</div>
</body>
</html>

