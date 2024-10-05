function toggleNotificationPanel() {
    const notificationPanel = document.getElementById('notificationPanel');
    notificationPanel.style.display = notificationPanel.style.display === 'block' ? 'none' : 'block';

    // Get the location of the notification icon
    const notificationIcon = document.querySelector('.notification-icon');
    const iconRect = notificationIcon.getBoundingClientRect();

    // Adjust the position of the notification panel
    notificationPanel.style.top = `calc(${iconRect.bottom}px + 10px)`;
    notificationPanel.style.left = `calc(${iconRect.left}px - 170px)` // Adjust as needed
}


function logOut(){
    $.ajax({
        url: "/LogOutServlet",
        type: "POST",
        data: {},

        success: function (response) {
            console.log('success');
            window.location.href = "index.jsp";
        },
        error: function (xhr, status, error) {
            console.error("AJAX Request Error:", error);
            console.error("Status:", status);
            console.error("XHR Object:", xhr);
        }
    });
}