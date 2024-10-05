CREATE DATABASE test_quiz;
USE test_quiz;

DROP TABLE IF EXISTS USERS;

CREATE TABLE USERS (
                       ID int NOT NULL AUTO_INCREMENT,
                       Username VARCHAR(255) NOT NULL UNIQUE,
                       Email VARCHAR(60) NOT NULL UNIQUE,
                       Password_hash VARCHAR(255) NOT NULL,
                       Is_administrator BOOLEAN NOT NULL,
                       Date_added DATETIME DEFAULT CURRENT_TIMESTAMP(),
                       PRIMARY KEY (ID)
);

INSERT INTO USERS(Username, Email, Password_hash,Is_administrator)
VALUES ("user1", "u1@test.com", "password", true);

INSERT INTO USERS(Username, Email, Password_hash,Is_administrator)
VALUES ("user2", "u2@test.com", "password2", false);

INSERT INTO USERS(Username, Email, Password_hash,Is_administrator)
VALUES ("user3", "u3@test.com", "password3", false);

DROP TABLE IF EXISTS QUIZZES;

CREATE TABLE QUIZZES(
                        ID int NOT NULL AUTO_INCREMENT,
                        Author int not null,
                        Quiz_name VARCHAR(255) NOT NULL,
                        Descr VARCHAR(400) NOT NULL,
                        Timer int NOT NULL,
                        Date_created DATETIME DEFAULT CURRENT_TIMESTAMP(),
                        Is_random BOOLEAN NOT NULL DEFAULT FALSE,
                        Display_type BOOLEAN NOT NULL DEFAULT FALSE,
                        Corrects_immediately BOOLEAN NOT NULL DEFAULT FALSE,
                        Is_practice BOOLEAN NOT NULL DEFAULT FALSE,
                        FOREIGN KEY (Author) REFERENCES USERS(ID) ON DELETE CASCADE,
                        PRIMARY KEY (ID)
);

ALTER TABLE QUIZZES ADD Category VARCHAR(50) NOT NULL;

alter table QUIZZES modify column Category VARCHAR(50) NOT NULL after Timer;

DROP TABLE IF EXISTS QUESTIONS;

CREATE TABLE QUESTIONS (
                           question_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                           quiz_id INT NOT NULL,
                           question_text VARCHAR(255),
                           question_type VARCHAR(50) NOT NULL,
                           timer INT,
                           FOREIGN KEY (quiz_id) REFERENCES QUIZZES(ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS QUESTION_RESPONSE;

CREATE TABLE QUESTION_RESPONSE (
                                   question_id INT,
                                   response_answer VARCHAR(255) NOT NULL,
                                   FOREIGN KEY (question_id) REFERENCES QUESTIONS(question_id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS MULTI_ANSWER;

CREATE TABLE MULTI_ANSWER (
                              question_id INT PRIMARY KEY,
                              is_ordered BOOLEAN NOT NULL,
                              num_fields INT NOT NULL,
                              FOREIGN KEY (question_id) REFERENCES QUESTIONS(question_id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS MULTI_ANSWER_ANSWERS;

CREATE TABLE MULTI_ANSWER_ANSWERS (
                                      answer_id INT PRIMARY KEY AUTO_INCREMENT,
                                      question_id INT NOT NULL,
                                      answer_text VARCHAR(255) NOT NULL,
                                      answer_order INT,
                                      FOREIGN KEY (question_id) REFERENCES MULTI_ANSWER(question_id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS MATCHING;

CREATE TABLE MATCHING (
                          question_id INT NOT NULL,
                          source VARCHAR(255) NOT NULL,
                          target VARCHAR(255) NOT NULL,
                          FOREIGN KEY (question_id) REFERENCES QUESTIONS(question_id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS MULTIPLE_CHOICE;

CREATE TABLE MULTIPLE_CHOICE (
                                 question_id INT NOT NULL,
                                 option_text VARCHAR(255) NOT NULL,
                                 is_answer BOOLEAN NOT NULL,
                                 FOREIGN KEY (question_id) REFERENCES QUESTIONS(question_id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS HISTORY;

CREATE TABLE HISTORY(
                        ID int NOT NULL AUTO_INCREMENT,
                        Quiz_ID int NOT NULL,
                        User_ID int NOT NULL,
                        score int NOT NULL,
                        Time_relapsed int NOT NULL,
                        Date_taken DATETIME DEFAULT CURRENT_TIMESTAMP(),
                        PRIMARY KEY(ID),
                        FOREIGN KEY (User_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
                        FOREIGN KEY (Quiz_ID) REFERENCES QUIZZES(ID) ON DELETE CASCADE

);

INSERT INTO HISTORY(Quiz_ID, User_ID, score,Time_relapsed)
VALUES (30, 1, 10, 15);

INSERT INTO HISTORY(Quiz_ID, User_ID, score,Time_relapsed)
VALUES (30, 1, 15, 15);

INSERT INTO HISTORY(Quiz_ID, User_ID, score,Time_relapsed)
VALUES (30, 1, 20, 20);

INSERT INTO HISTORY(Quiz_ID, User_ID, score,Time_relapsed)
VALUES (30, 1, 15, 30);

INSERT INTO HISTORY(Quiz_ID, User_ID, score,Time_relapsed)
VALUES (35, 1, 10, 15);

INSERT INTO HISTORY(Quiz_ID, User_ID, score,Time_relapsed)
VALUES (35, 2, 10, 10);

INSERT INTO HISTORY(Quiz_ID, User_ID, score,Time_relapsed)
VALUES (35, 1, 12, 10);

INSERT INTO HISTORY(Quiz_ID, User_ID, score,Time_relapsed)
VALUES (40, 1, 10, 15);

INSERT INTO HISTORY(Quiz_ID, User_ID, score,Time_relapsed)
VALUES (40, 1, 10, 15);

DROP TABLE IF EXISTS RATING;

CREATE TABLE RATING (
                        ID int NOT NULL AUTO_INCREMENT,
                        PRIMARY KEY(ID),
                        User_ID int NOT NULL,
                        Quiz_ID int NOT NULL,
                        Rating double precision NOT NULL,
                        CONSTRAINT un unique (User_ID, Quiz_ID),
                        FOREIGN KEY (User_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
                        FOREIGN KEY (Quiz_ID) REFERENCES QUIZZES(ID) ON DELETE CASCADE
);


DROP TABLE IF EXISTS REVIEW;

CREATE TABLE REVIEW (
                        ID int NOT NULL AUTO_INCREMENT,
                        PRIMARY KEY(ID),
                        User_ID int NOT NULL,
                        Quiz_ID int NOT NULL,
                        Content VARCHAR(1000) NOT NULL,
                        Time_reviewed DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT un unique (User_ID, Quiz_ID),
                        FOREIGN KEY (User_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
                        FOREIGN KEY (Quiz_ID) REFERENCES QUIZZES(ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS TAG;

CREATE TABLE TAG (
                     ID int NOT NULL AUTO_INCREMENT,
                     PRIMARY KEY(ID),
                     Quiz_ID int NOT NULL,
                     Hashtag VARCHAR(100) NOT NULL,
                     CONSTRAINT un unique  (Quiz_ID,Hashtag),
                     FOREIGN KEY (Quiz_ID) REFERENCES QUIZZES(ID) ON DELETE CASCADE
);