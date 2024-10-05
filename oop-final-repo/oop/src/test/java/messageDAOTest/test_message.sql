DROP DATABASE IF EXISTS test_message  ;
CREATE DATABASE test_message;
USE test_message ;

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

