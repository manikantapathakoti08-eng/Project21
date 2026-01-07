# Project21

Full-stack Quiz Application — Spring Boot REST API backend with an Angular frontend.

## Overview

Project21 is a full-stack quiz application. The backend is implemented with Spring Boot (Java) and exposes REST endpoints for quizzes, questions, and results. The frontend is an Angular single-page application that consumes the backend API to present quizzes, collect answers, and display results.

## Features

- Create, fetch and run quizzes and questions via REST controllers (`QuizController`, `QuestionController`).
- Store and compute quiz results (`Quiz`, `QuizResult`, `QuestionWrapper`, `UserAnswer`).
- Angular UI for taking quizzes and viewing results (files under `FrontEnd/src/app/quiz`).

## Tech stack

- Backend: Java, Spring Boot, Maven
- Frontend: Angular (TypeScript)

## Quick start

### Backend

```powershell
cd BackEnd
./mvnw spring-boot:run
# or: mvn spring-boot:run

cd FrontEnd
npm install
npm start
# or: ng serve --open
