# Quiz Playing Application

The Quiz Playing Application is a web platform that allows users to create and play quizzes in real-time. It provides a user interface for quiz creators to design quizzes and for players to participate in quizzes.

## Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [Usage](#usage)
- [License](#license)

## Features

- Quiz creators can design quizzes and manage questions and answers.
- Players can join active quizzes using a unique PIN.
- Real-time communication between quiz admins and players using websocket technology.
- Timer for each question with visual countdown.
- Tracking scores and displaying leaderboards.
- Saving quiz results as XLS files.

## Technologies Used

- Backend: Java, Jetty Server, JAX-RS, JPA (Java Persistence API)
- Frontend: HTML, CSS, JavaScript
- Websockets for real-time communication
- Database: MySQL
- Build and Dependency Management: Maven
- Other libraries and frameworks: Hibernate, Jersey, jQuery, Bootstrap

## Installation

1. Clone the repository: `git clone https://github.com/blasted-tiara/quiz-app`
2. Set up the database and configure the connection details in `src/main/resources/hibernate.cfg.xml`.
3. Build the project using Maven: `mvn clean install`
4. Populate the database by running the `main` function of the class `src/main/java/ba/fet/rwa/scripts/UserDataLoader.java`.
4. Start the Jetty server: `mvn jetty:run`
5. Access the application in your web browser at `http://localhost:8080`. (Admin page: `http://localhost:8080/admin`, Player page: `http://localhost:8080/player/index.html`)

## Usage

1. Create a Quiz:
    - Log in as a quiz admin.
    - Design and configure the quiz by adding questions and answers.
    - Generate a unique PIN to share with players.

2. Play the Quiz:
    - Players access the quiz page and enter the provided PIN.
    - Wait for the quiz admin to start the quiz.
    - Answer the questions within the given time limit.
    - View the scores and leaderboard after each question.
    - Final results are displayed at the end of the quiz.

## Known Bugs

The following bugs are present in the current version of the application:
   - Username of the administrator/moderator account is not unique
   - When deleting a quiz or changing an image, the uploaded image is not deleted from the server.
