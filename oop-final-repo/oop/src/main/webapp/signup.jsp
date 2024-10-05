<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <link rel="stylesheet" type="text/css" href="css/authentication.css">
    <script src="js/authentication.js"></script>

    <title>Sign Up</title>

</head>
<body>
<div class="signin-container">
    <div class="signin-header">
        <h2>Join to Quiz app</h2>
    </div>
    <form class="signin-form" action="RegisterServlet" method="POST">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" placeholder="Enter your username" required>
        </div>

        <div class="form-group">
            <label for="username">Email</label>
            <input type="text" id="email" name="email" placeholder="Enter your mail" required>
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <div class="input-icon">
                <input type="password" id="password" name="password" placeholder="Enter your password" required>
                <button type="button" class="toggle-password">
                    <i class="bi bi-eye"></i>
                </button>
            </div>
        </div>

        <div><button class="signin-button">Register</button></div>
    </form>

</div>

</body>
</html>