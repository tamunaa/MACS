const userList = document.getElementById('user-list');
const chatContainer = document.getElementById('chat-container');
const chatMessages = document.getElementById('chat-messages');
const messageInput = document.getElementById('message-input');
const sendButton = document.getElementById('send-button');

const userChats = {
    'User 1': ["user1"],
    'User 2': ["user2"],
    'User 3': ["user3"]
};
// out.print(userChats);

function updateChat(user) {
    chatMessages.innerHTML = '';
    const userChat = userChats[user];
    userChat.forEach((message) => {
        const messageElement = document.createElement('div');
        messageElement.classList.add('message', 'user-message');
        messageElement.textContent = message;
        chatMessages.appendChild(messageElement);
    });
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function sendMessage(user) {
    const messageText = messageInput.value;
    if (messageText.trim() !== '') {
        userChats[user].push(messageText);
        updateChat(user);
        messageInput.value = '';
    }
}

userList.addEventListener('click', (event) => {
    if (event.target.classList.contains('user')) {
        const selectedUser = event.target.getAttribute('data-user');
        updateChat(selectedUser);
    }
});

sendButton.addEventListener('click', () => {
    const selectedUser = document.querySelector('.user.active').getAttribute('data-user');
    sendMessage(selectedUser);
});

messageInput.addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        const selectedUser = document.querySelector('.user.active').getAttribute('data-user');
        sendMessage(selectedUser);
    }
});
userList.addEventListener('click', (event) => {
    if (event.target.classList.contains('user')) {
        const users = document.querySelectorAll('.user');
        users.forEach((user) => {
            user.classList.remove('active');
        });
        event.target.classList.add('active');
    }
});


