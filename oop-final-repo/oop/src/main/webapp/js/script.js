function showCreatedQuizzes() {
    const dynamicContent = document.getElementById('dynamic-content');
    dynamicContent.innerHTML = `
        <ul>
            <li>Quiz 1</li>
            <li>Quiz 2</li>
            <!-- Add more created quizzes here -->
        </ul>
    `;
}

function showTakenQuizzes() {
    const dynamicContent = document.getElementById('dynamic-content');
    dynamicContent.innerHTML = `
        <ul>
            <li>Quiz A</li>
            <li>Quiz B</li>
            <!-- Add more taken quizzes here -->
        </ul>
    `;
}

function showAchievements() {

    const dynamicContent = document.getElementById('dynamic-content');
    dynamicContent.innerHTML = `
        <ul>
            <li>Achievement A</li>
            <li>Achievement B</li>
        </ul>
    `;
}

function showFriends() {

    const dynamicContent = document.getElementById('dynamic-content');
    dynamicContent.innerHTML = `
        <ul>
            <li>Friend A</li>
            <li>Friend B</li>
        </ul>
    `;
}