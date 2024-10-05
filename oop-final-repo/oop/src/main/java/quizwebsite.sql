DROP DATABASE IF EXISTS oopquizzweb  ;
CREATE DATABASE oopquizzweb  ;
USE oopquizzweb ;

DROP TABLE IF EXISTS USERS;

CREATE TABLE USERS (
                       ID int NOT NULL AUTO_INCREMENT,
                       Username VARCHAR(255) NOT NULL UNIQUE,
                       Email VARCHAR(60) NOT NULL UNIQUE,
                       Password_hash VARCHAR(255) NOT NULL,
                       Is_administrator BOOLEAN NOT NULL DEFAULT FALSE,
                       Date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                       PRIMARY KEY (ID)
);

DROP TABLE IF EXISTS QUIZZES;

CREATE TABLE QUIZZES(
                        ID int NOT NULL AUTO_INCREMENT,
                        Author int not null,
                        Quiz_name VARCHAR(255) NOT NULL,
                        Descr VARCHAR(400) NOT NULL,
                        Timer int NOT NULL,
                        Category VARCHAR(50) NOT NULL,
                        Date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                        Is_random BOOLEAN NOT NULL DEFAULT FALSE,
                        Display_type BOOLEAN NOT NULL DEFAULT FALSE,
                        Corrects_immediately BOOLEAN NOT NULL DEFAULT FALSE,
                        Is_practice BOOLEAN NOT NULL DEFAULT FALSE,
                        FOREIGN KEY (Author) REFERENCES USERS(ID) ON DELETE CASCADE,
                        PRIMARY KEY (ID)
);

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

DROP TABLE IF EXISTS PICTURE_RESPONSE;

CREATE TABLE PICTURE_RESPONSE (
                                  question_id INT NOT NULL,
                                  question_URL VARCHAR(255) NOT NULL,
                                  response_answer VARCHAR(255) NOT NULL,
                                  FOREIGN KEY (question_id) REFERENCES QUESTIONS(question_id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS FRIENDS;

CREATE TABLE FRIENDS(
                        ID int NOT NULL AUTO_INCREMENT,
                        User_ID int NOT NULL,
                        Friend_ID int NOT NULL,
                        PRIMARY KEY(ID),
                        CONSTRAINT un unique (User_ID, Friend_ID),
                        CHECK (User_ID != Friend_ID),
                        FOREIGN KEY (User_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
                        FOREIGN KEY (Friend_ID) REFERENCES USERS(ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS FRIEND_REQS;

CREATE TABLE FRIEND_REQS(
                        ID int NOT NULL AUTO_INCREMENT,
                        Reciever_ID int NOT NULL,
                        Sender_ID int NOT NULL,
                        PRIMARY KEY(ID),
                        CONSTRAINT un unique (Reciever_ID, Sender_ID),
                        CHECK (Reciever_ID != Sender_ID),
                        FOREIGN KEY (Sender_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
                        FOREIGN KEY (Reciever_ID) REFERENCES USERS(ID) ON DELETE CASCADE

);

DROP TABLE IF EXISTS MESSAGE;


CREATE TABLE MESSAGE(
                     ID int NOT NULL AUTO_INCREMENT PRIMARY KEY,
                     Sender_ID int NOT NULL,
                     Reciever_ID int NOT NULL,
                     Message_type VARCHAR(10) NOT NULL,
                     Message_content VARCHAR(255),
                     Date_sent TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),

                     CHECK (Sender_ID != Reciever_ID),
                     FOREIGN KEY (Sender_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
                     FOREIGN KEY (Reciever_ID) REFERENCES USERS(ID) ON DELETE CASCADE
);

DROP TABLE IF EXISTS CHALLENGES;

CREATE TABLE CHALLENGES(
                            ID int NOT NULL AUTO_INCREMENT,
                            Challenger_ID int NOT NULL,
                            Challenged_ID int NOT NULL,
                            Quiz_ID int NOT NULL,
                            Date_sent TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                            PRIMARY KEY(ID),
                            CONSTRAINT un unique (Challenger_ID, Challenged_ID, Quiz_ID),
                            FOREIGN KEY (Challenger_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
                            FOREIGN KEY (Challenged_ID) REFERENCES USERS(ID) ON DELETE CASCADE

);

DROP TABLE IF EXISTS ACHIEVEMENTS;

CREATE TABLE ACHIEVEMENTS(
                                 ID int NOT NULL AUTO_INCREMENT,
                                 Achievement_type VARCHAR(255) NOT NULL,
                                 Picture_url VARCHAR(300) NOT NULL,
                                 Achievement_desc VARCHAR(700) NOT NULL,
                                 PRIMARY KEY(ID)

);

DROP TABLE IF EXISTS USER_ACHIEVEMENT;

CREATE TABLE USER_ACHIEVEMENT(
                            ID int NOT NULL AUTO_INCREMENT,
                            User_ID int NOT NULL,
                            Achievement_ID int NOT NULL,
                            PRIMARY KEY(ID),
                            CONSTRAINT un unique (User_ID, Achievement_ID),
                            FOREIGN KEY (User_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
                            FOREIGN KEY (Achievement_ID) REFERENCES ACHIEVEMENTS(ID) ON DELETE CASCADE

);

DROP TABLE IF EXISTS ANNOUNCEMENTS;

CREATE TABLE ANNOUNCEMENTS(
                        ID int NOT NULL AUTO_INCREMENT,
                        User_ID int NOT NULL,
                        Creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                        Announcement_title VARCHAR(50) NOT NULL,
                        Announcement_text VARCHAR(800) NOT NULL,
                        FOREIGN KEY (User_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
                        PRIMARY KEY (ID)
);



DROP TABLE IF EXISTS HISTORY;

CREATE TABLE HISTORY(
                           ID int NOT NULL AUTO_INCREMENT,
                           Quiz_ID int NOT NULL,
                           User_ID int NOT NULL,
                           score int NOT NULL DEFAULT 0,
                           Time_relapsed double precision NOT NULL,
                           Date_taken TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                           PRIMARY KEY(ID),
                           FOREIGN KEY (User_ID) REFERENCES USERS(ID) ON DELETE CASCADE,
                           FOREIGN KEY (Quiz_ID) REFERENCES QUIZZES(ID) ON DELETE CASCADE

);

DROP TABLE IF EXISTS RESPONSES;

CREATE TABLE RESPONSES(
                           ID int NOT NULL AUTO_INCREMENT,
                           Question_ID int NOT NULL,
                           History_ID int NOT NULL,
                           grade int NOT NULL,
                           Is_graded BOOLEAN NOT NULL,
                           Response VARCHAR(300) NOT NULL,
                           PRIMARY KEY(ID),
                           CONSTRAINT un unique (Question_ID, History_ID),
                           FOREIGN KEY (Question_ID) REFERENCES QUESTIONS(question_id) ON DELETE CASCADE,
                           FOREIGN KEY (History_ID) REFERENCES HISTORY(ID) ON DELETE CASCADE

);

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
                        Time_reviewed TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
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

DROP TABLE IF EXISTS CATEGORY;

CREATE TABLE CATEGORY(
                         ID int NOT NULL AUTO_INCREMENT,
                         PRIMARY KEY(ID),
                         User_ID int NOT NULL,
                         Category VARCHAR(50) NOT NULL UNIQUE ,
                         FOREIGN KEY (User_ID) REFERENCES USERS(ID) ON DELETE CASCADE

)










