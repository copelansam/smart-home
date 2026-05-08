# Smart Home Simulator Project
***

## Introduction 
This project serves as the semester long project for my Object-Oriented Design (SWE 4743) Class.
This project is a simulation of a smart home management application that will allow users to view the current status of their various smart devices (fans, doors, lights, etc) and update their status.
Examples of actions that can be performed include turn applicable devices on/off, set the color of lights, etc.

***

## Learning Objectives
This project will make use of the following principles of design & Software Engineering Tools:
- All SOLID principles
- Various OO design patterns including state machine, strategy, and factory
- Docker for containerization and deployment 
- Unit Testing for validating core application logic 

***

## Features
This project provides support for a wide variety of features including:
- View devices grouped by location.
- Retrieve devices using filters for device type, location, and power status.
- Control a series of smart devices within the simulated smart home.
- Create new devices and delete existing devices
- View a device's history including client requested commands and internal history
- Simulate thermostat behavior using controllable ambient temperature and simulation speed

***

## Tech Stack

| Area | Technology |
|------|-------|
| Front End | Angular |
| Backend | Spring Boot |
| Database | SQLite |
| ORM | Nibernate |
| API Testing | Bruno |

***

## How to Build & Run the Application
There are 2 main methods to run this application:
1. Docker
   - This application can be built and ran with docker.
   - To run the application with docker, do the following:
     1. Clone the repository by running `git clone https://github.com/copelansam/smart-home` in your CLI
     2. Ensure that Docker is installed on your machine. You can install Docker from [here](https://www.docker.com/get-started/)
     3. From the CLI, navigate to the application's root directory
     4. Build the Docker image by running `docker compose up`
     5. Access the application by visiting http://localhost:4200 in your browser of choice
     6. When you are finished with the application, end it by running `docker compose down` in the CLI


2. Build & Run Locally for Development
   - This application can be built and run locally for development.
   - To run the application locally you need to do the following:
     1. Ensure you have maven installed. Info regarding how to install it can be found [here](https://maven.apache.org/install.html)
     2. Ensure that you have Java version 20 or higher installed. Info regarding how to install java can be found [here](https://www.java.com/en/download/help/download_options.html)
     3. Ensure that you have Node.js installed. Info regarding how to install it can be found [here](https://nodejs.org/en/download)
     4. Ensure that you have npm installed. Info regarding how to install it can be found [here](https://docs.npmjs.com/downloading-and-installing-node-js-and-npm) and Angular CLI installed
     5. Ensure that you have Angular CLI. Info regarding how to install it can be found [here](https://angular.dev/installation)
     6. From the CLI, navigate to the backend directory and run `mvn spring-boot:run` to build and run the back end
     7. From the CLI, navigate to the frontend directory adn run `ng serve` to build and run the front end
     8. Once both the front and back end are running, you can access the application by visiting http://localhost:4200  in your browser of choice

***

## How to Access the Application

### UI Access
-  http://localhost:4200

### Swagger Documentation
- http://localhost:8080/swagger-ui/index.html

### API Base URL
- http://localhost:8080/api

***

## API Endpoints

### Devices
- GET `/devices` — Retrieve all devices
- GET `/devices/{id}` — Retrieve a device by ID
- POST `/create-device` — Create a device
- DELETE `/devices/{id}` — Delete a device

### Device Actions
- PUT `/devices/{id}/state` — Execute state machine action

### Logs
- GET `/devices/{id}/history` — Retrieve device logs

### Simulation Controls
- POST `/simulation/speed` — Update simulation speed
- POST `/simulation/location/temperature` — Update temperature
- POST `/simulation/reset` — Factory reset all devices

***

## Running Tests

This application supports API tests with Bruno. To run the bruno tests, run the backend, navigate to the `bruno` directory in the CLI and run `bru run --env tests`

***

## OO Design Patterns
This project makes use of several OO Design patterns which will be documented below:
- State: 
  - Each smart device is controlled by its own statechart machine which determines what actions a device may perform 
while in each state. For more info, click [here](backend/src/main/java/com/example/smarthome/domain/smartdevices/statemachine/README.md)
- Factory:
  - The factory pattern is used in two different locations: [device creation](backend/src/main/java/com/example/smarthome/domain/smartdevices/devicefactories/README.md)
and [thermostat strategy](backend/src/main/java/com/example/smarthome/simulation/strategies/README.md) selection. 
  - This pattern was implemented in both of these areas to help centralize object creation logic
- Strategy:
  - The strategy pattern is implemented in the [thermostat simulation](backend/src/main/java/com/example/smarthome/simulation/strategies/README.md) 
feature. It is specifically used to dynamically choose thermostat behavior during runtime
- Repository:
  - This project makes use Object-Relational Mapping (ORM) & the [repository pattern](backend/src/main/java/com/example/smarthome/repository/README.md) 
to help store data without needing to worry about writing SQL statements
  - Services are able to rely on an abstraction that promises common features (save, update, delete, etc.) instead of having to keep track of different SQL queries
- Decorator:
  - The [decorator pattern](backend/src/main/java/com/example/smarthome/domain/devicequeries/README.md) is used while filtering device queries by type, location, and power status. 
  - It allows the system to dynamically alter its behavior without the need for a large number of classes
