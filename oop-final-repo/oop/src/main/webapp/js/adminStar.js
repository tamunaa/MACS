let starElement = document.getElementsByName("star")[0];
console.log("star element", starElement);
starElement.addEventListener("click", function() {
    let isFilled = starElement.classList.contains('filled');
    let path = '/MakeAdminServlet';
    if (isFilled) {
        path = '/RemoveAdminServlet';
        starElement.classList.remove('filled');
    } else {
        starElement.classList.add('filled');
    }
    $.ajax({
        url: path,
        type: "POST",
        data: {"newAdmin": currentUser },

        success: function (response) {
            console.log('success');
        },
        error: function (xhr, status, error) {
            console.error("AJAX Request Error:", error);
            console.error("Status:", status);
            console.error("XHR Object:", xhr);
        }
    });
});