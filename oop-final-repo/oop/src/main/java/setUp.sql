DELETE FROM ACHIEVEMENTS where 1=1;

INSERT INTO ACHIEVEMENTS (Achievement_type, Picture_url, Achievement_desc)
VALUES
    ('amateur', 'photos/amateur.jpeg', 'The user created a quiz'),
    ('I am the Greatest', 'photos/I am the Greatest.png',
     'The user had the highest score on a quiz. Note, once earned this achievement does not go away if someone else later bests the high score.'),
    ('Practice Makes Perfect', 'photos/Practice Makes Perfect.jpg', 'The user took a quiz in practice mode'),
    ('Prodigious Author', 'photos/Prodigious Author.jpg', 'The user created ten quizzes.'),
    ('Profilic Author', 'photos/Profilic Author.jpg', 'The user created five quizzes.'),
    ('Quiz Machine', 'photos/Quiz Machine.jpg', 'The user took ten quizzes.');
;

DELETE FROM USERS where 1=1;

INSERT INTO USERS (Username, Email, Password_hash, Is_administrator)
VALUES
    ('admin', 'admin@example.com', '$2a$10$HXYIgiSCyzVADd6GEqv.9OOKLciRfghOKTVNFtQHgbOEOezDDYIY2', 1),
    ('test1', 'test1@example.com', '$2a$10$HXYIgiSCyzVADd6GEqv.9OOKLciRfghOKTVNFtQHgbOEOezDDYIY2', 0),
    ('test2', 'test2@example.com', '$2a$10$HXYIgiSCyzVADd6GEqv.9OOKLciRfghOKTVNFtQHgbOEOezDDYIY2', 0)
;
