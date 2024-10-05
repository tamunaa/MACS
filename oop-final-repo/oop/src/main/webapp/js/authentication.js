const togglePasswordButton = document.querySelector('.toggle-password');
const passwordInput = document.getElementById('password');

togglePasswordButton.addEventListener('click', function() {
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
    } else {
        passwordInput.type = 'password';
    }
});