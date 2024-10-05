CREATE DATABASE test_achievement;
USE test_achievement;

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
VALUES ("user1", "u1@test.com", "password", true);

INSERT INTO USERS(Username, Email, Password_hash,Is_administrator)
VALUES ("user2", "u2@test.com", "password2", false);

INSERT INTO USERS(Username, Email, Password_hash,Is_administrator)
VALUES ("user3", "u3@test.com", "password3", false);

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