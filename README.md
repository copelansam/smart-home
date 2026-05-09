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
- Using AI tools to aid in the development process (specifically with testing)

***

## Video Presentations

- [UI Presentation](https://mediaspace.kennesaw.edu/media/SWE%204743%20Semester%20Project%20UI%20Presentation/1_q4xzidh9)
- [Architecture Presentation](https://mediaspace.kennesaw.edu/media/SWE%204743%20Semester%20Project%20Architecture%20Presentation/1_xpnnfpc0)


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

| Area              | Technology  |
|-------------------|-------------|
| Front End         | Angular     |
| Back End          | Spring Boot |
| Database          | SQLite      |
| ORM               | Hibernate   |
| Component Library | PrimeNG     |  
| API Testing       | Bruno       |

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

## Example API Calls with Payloads

### Turn Light On

URL: `PUT /devices/{id}/state?action=TURN_LIGHT_ON`

Body: None (body is used to pass in optional parameters that are used by some state transitions (primarily those that update device attributes))

\* Performs the TURN_LIGHT_ON action on the device with the specified id. 
(If the device is a light in the off state, it will turn the light on. Otherwise, nothing will happen)

\* A list of all available actions for each device and state can be found [here](backend/src/main/java/com/example/smarthome/domain/smartdevices/statemachine/README.md)

### Update Thermostat Mode

URL: `PUT /devices/{id}/state?action=UPDATE_MODE`

Body: 
`{
"mode" : "HEAT"
}
`
\* Performs the UPDATE_MODE action on the device with the specified id and specified "mode" value in the body.
(If the device is a thermostat, it will change its mode to HEAT. Otherwise, nothing will happen)

### Update Simulation Speed

URL: `PUT /simulation/speed?timeMultiplier=2.0`

\* Updates the application's simulation speed rate to 2x

### Factory Reset all Devices

URL: `POST /simulation/reset`

\* Sets all devices to their factory settings

### Create a new Thermostat

URL: `POST /devices/create-device`

Body: 
`{
"name": "Example Thermostat",
"type": "THERMOSTAT",
"location": "Bathroom" }
`

\* Creates a new thermostat device in the bathroom if that location does not already have a thermostat. 
If it already has one, the API call does not create a device 

## Running Tests

This application supports units tests, API tests, and frontend tests, to execute each testing suite do the following:
- Unit Tests: navigate to the `/backend` directory in the CLI and run `mnv test` to execute the tests
- API Tests: While the backend is running, navigate to the `/bruno` directory in the CLI and run `bru run --env tests` to execute the API tests
- Front End Tests: navigate to the `/frontend` directory in the CLI and run `ng test` to execute the frontend tests

***

## OO Design Patterns

This project makes use of several OO Design patterns which will be documented below:

| Pattern    | Location                                                                                                      | Justification                                                                                                                                                                                                                                              |
|------------|---------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| State      | `backend/src/main/java/com/example/smarthome/domain/smartdevices/statemachine`                                | Each smart device is controlled by its own statechart machine which determines what actions a device may perform while in each state. For more info, click [here](backend/src/main/java/com/example/smarthome/domain/smartdevices/statemachine/README.md)  |
| Factory    | `backend/src/main/java/com/example/smarthome/domain/smartdevices/devicefactories` & `smarthome/simulation/strategies/ThermostatStrategyFactory` | Help centralize creation logic for [smart devices](backend/src/main/java/com/example/smarthome/domain/smartdevices/devicefactories/README.md) and [thermostat strategies](backend/src/main/java/com/example/smarthome/simulation/strategies/README.md)     |
| Strategy   | `backend/src/main/java/com/example/smarthome/simulation/strategies/*`                                                                           | Allow the system to dynamically determine how a thermostate should behave in the simulation based on factors unknown before runtime. More info [here](backend/src/main/java/com/example/smarthome/simulation/strategies/README.md)                         |
| Repository | `backend/src/main/java/com/example/smarthome/repository/DeviceLogRepository` & `smarthome/repository/ISmartDeviceRepository`                    | Enables the use of ORM mapping by hiding persistence logic behind and interface. Service do not need to know how domain objects are saved/ deleted, just that they are. More info [here](backend/src/main/java/com/example/smarthome/repository/README.md) |
| Decorator  | `backend/src/main/java/com/example/smarthome/domain/devicequeries/*`                                                                            | Allows the system to dynamically alter its device retireval behavior without the need for a large number of classess. More info [here](backend/src/main/java/com/example/smarthome/domain/devicequeries/README.md)                                                                                                                   |


***

## Team Size & Extra Credit

My team consisted of 1 person (Myself)

This project implements the Object-Relational Mapping (ORM) extra credit opportunity. 
Device and Log objects are mapped directly to tables within the database. 
The application services utilize Hibernate and the Repository Pattern to manage records.
By abstracting the data access layer, this approach ensures a clean separation between business logic and persistence logic.


***

## Known Bugs

- While filtering devices in the UI, if a location is not included in the results it will not be selectable in 
the location select filter or update temperature dropdown menus. This is because the frontend retrieves the list 
of locations from the devices that are retrieved from the backend. 
When filters are applied and a location is not included, it is dropped from the locations list. 
You can get around this hitting the reset filters button which will send a default device retrieval 
request and return all devices with all locations.
- If you filter by device type and a thermostat is in heating or cooling, it will appear in the device list on the 
front end. The websocket sends the updated thermostat to the front end, and it gets displayed in the UI. 
Currently no work around as I am discovering this bug a few hours before the project is due.

***

## Future Refinements

While this project is for a college course, I plan to keep developing this project into a more refined portfolio piece.
I plan on making some adjustments to better align with modern software engineering practices and principles. 

These changes include:
- Implementing a proper singleton pattern implementation. Currently, there are some classes that would work if they 
were placed in a singleton (simulation settings, websocket config)
- Implement a Device Log DTO class. Currently, I am exposing the Device Log itself. I forgot to make a DTO for it :( . 
- Updating the UI to have device settings inline with device information for a sleeker UI
- Add a CI/CD pipeline to get experience with it
- Fix bugs mentioned above


***

## AI Acknowledgement

Portions of this project were made with the help of AI Tools

The AI was used to help me understand some of the more abstract concepts that were introduced as a part of the course and project.
Some examples of prompts that would have been used during this project include:

- "How does ORM work?"
- "How can I make my API error responses meet the standards set by RFC 9457?"
- "How should I handle sending backend data through API calls?"
- "How does Angular work?"

Any instances of AI tools generating code that is used in the project will be annotated inline with the code

Any instances of AI tools generating larger parts of the project will be noted below:
- ChatGPT was used to generate most of this project's documentation (specifically the Javadoc comments) before being reviewed and edited by myself
- Calude Code was used to generate the backend unit tests and front ends tests for this application 
- in compliance with the assignment's instructions. They were reviewed by myself before acceptance.