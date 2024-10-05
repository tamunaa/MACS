# Quiz Website Project

Welcome to the Quiz Website Project! This web application is designed to facilitate the creation, management, and participation in quizzes. Whether you're a developer, contributor, or user, this README will guide you through the project's purpose, features, setup, and contribution guidelines.

- [Project Overview](#project-overview)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Getting Started](#getting-started)
  - [Installation](#installation)
  - [Database Setup](#database-setup)
  - [Build and Run](#build-and-run)
  - [Access the Web Application](#access-the-web-application)
  - [Interact with the Web Application](#interact-with-the-web-application)
  - [Initial User Setup](#initial-user-setup)
- [Contributing](#contributing)

**Project Overview**

The Quiz Website Project is a web application that empowers users to create, manage, and participate in quizzes. It provides an engaging platform for educational purposes, testing, and fun quizzes.
Technologies Used

### Backend

- **Maven:** Efficiently manages project dependencies and builds.
- **Java:** Powers the core backend logic.
- **MySQL:** A reliable database management system for data storage.

### Frontend

- **HTML:** Structures web content.
- **CSS:** Enhances the user interface.



## Features

The Quiz Website Project offers a range of features to enhance user engagement and interaction:

- **User Authentication:** Enables account creation, login, and profile management.
- **Sending Friend Requests:** Facilitates user connections and networking.
- **Quiz Creation:** Allows users to craft quizzes, add questions, and customize options.
- **Quiz Participation:** Engages participants in taking quizzes, answering questions, and viewing their scores.
- **Leaderboards:** Displays leaderboards to showcase top performers.
- **Admin Panel:** Empowers administrators to manage users, quizzes, and reported content.
- **Messaging Feature:** Provides a real-time messaging system for user communication.

## Getting Started

### Installation

To set up the project locally, follow these steps:

1. **Clone the Repository:**
   Clone the project repository to your local machine.

   ```bash
   git clone https://github.com/tamunaa/oop-final-repo.git
   cd oop-final-repo

## Database Setup
To set up the database for your project, follow these steps:

Set up a MySQL database for your project.
Update the database connection settings in the databaseInfo.java file.
Run the provided SQL scripts (quizwebsite.sql and setUp.sql) to create databases and initial data.


Build and Run:

Build the project using Maven. This will download dependencies, compile the source code, and generate artifacts.

    mvn clean package

Start the project using the embedded Tomcat server.

    mvn tomcat7:run

Access the Web Application:
    Open a web browser and navigate to the application, typically hosted at http://localhost:8080/.

Interact with the Web Application:
    Log in, create an account, or explore the application's functionality.

## Initial User Setup

Upon launching the application, three default users are added automatically:

- **Admin User:**
  - Username: `admin`
  - Password: `123`
  - Role: Administrator

- **Test User 1:**
  - Username: `test1`
  - Password: `123`
  - Role: Regular User

- **Test User 2:**
  - Username: `test2`
  - Password: `123`
  - Role: Regular User

For security, it is advisable to change the default passwords, except for the admin user, who has administrative privileges.


**Contributing**

We welcome contributions from the community! To contribute to this project:

Fork the repository.
Create a new branch:

    git checkout -b feature/your-feature-name
    
Make your changes and commit them: 

    git commit -m 'Add some feature'
    
Push your changes to the branch: 
    
    git push origin feature/your-feature-name
Open a pull request.


Thank you for being part of our project!
