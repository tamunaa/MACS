CREATE DATABASE test_challenge;
USE test_challenge;

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

INSERT INTO USERS(Username, Email, Password_hash,Is_administrator)
VALUES ("user1", "u1@test.com", "password1", true);

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
                        Category VARCHAR(50) NOT NULL,
                        Date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
                        Is_random BOOLEAN NOT NULL DEFAULT FALSE,
                        Display_type BOOLEAN NOT NULL DEFAULT FALSE,
                        Corrects_immediately BOOLEAN NOT NULL DEFAULT FALSE,
                        Is_practice BOOLEAN NOT NULL DEFAULT FALSE,
                        FOREIGN KEY (Author) REFERENCES USERS(ID) ON DELETE CASCADE,
                        PRIMARY KEY (ID)
);

INSERT INTO QUIZZES(Author, Quiz_name, Descr, Timer, Category) VALUES (1, "qvizi2", "satesto quiz" ,15, "physics");
INSERT INTO QUIZZES(Author, Quiz_name, Descr, Timer, Category, Is_random, Display_type) VALUES (2, "qvizi2", "martivi quiz", 30, "english", true, true);
INSERT INTO QUIZZES(Author, Quiz_name, Descr, Timer, Category, Corrects_immediately) VALUES (2, "qvizi", "sashualo quiz" ,40, "chemistry", true);
INSERT INTO QUIZZES(Author, Quiz_name, Descr, Timer, Category) VALUES (2, "qvizi", "sashualo quiz" ,35, "physics");
INSERT INTO QUIZZES(Author, Quiz_name, Descr, Timer, Category, Is_practice) VALUES (1, "qvizi", "satesto quiz" ,20, "math", true);
INSERT INTO QUIZZES(Author, Quiz_name, Descr, Timer, Category) VALUES (3,"qvizi","hard quiz", 50, "geography");

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