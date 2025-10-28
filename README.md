# ModuCrafter - Talent Project Mapping and Interview Tracking System

This repository contains the source code for the **ModuCrafter** application, which serves as a talent mapping and interview tracking solution. It consists of a frontend built with Angular and a backend powered by Spring Boot.

---

## 💻 Frontend Source (Angular)

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 20.1.4.

### Development Server

To start a local development server, run:

```bash
ng serve
````

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

### Code Scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

### Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory.

### Running Tests

* **Unit Tests:** To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use: `ng test`
* **End-to-End Tests:** For end-to-end (e2e) testing, use: `ng e2e`

-----

## 🚀 Backend Source (Spring Boot)

The backend provides all API endpoints, handles data persistence, Tika-based resume parsing, and asynchronous notification logic.

### Prerequisites

* Java Development Kit (JDK) 17+
* Maven (or Gradle)
* An IDE (IntelliJ IDEA or VS Code)
* **Database:** Configured for H2 (in-memory, default) or MySQL/PostgreSQL in `application.properties`.

### Running the Backend

1.  **Build the Project:** Compile the application using Maven.
    ```bash
    mvn clean install
    ```
2.  **Run the Application:** Start the Spring Boot application.
    ```bash
    mvn spring-boot:run
    ```

The backend will typically start on port **8080**.

### Key Backend Features

* **Asynchronous Notifications:** Uses Spring Events (`@Async` / `@EventListener`) to instantly alert BAs, AMSs, and AMCs on profile changes, mapping completion, and interview updates.
* **Authorization Simulation:** Implements a table-driven authorization check based on the `X-Auth-User-Id` header and the user's `ROLE` (BA, AMS, AMC) in the database.
* **Tika Integration:** Uses the Apache Tika library to parse resumes during profile submission.

### Core REST API Endpoints

The API base URL is `http://localhost:8080/api/`.

| Component | Endpoint | Method | Description | Roles |
| :--- | :--- | :--- | :--- | :--- |
| **Authentication** | `/auth/login` | `POST` | Simulates login via `username/password` to return user details (`empId`, `role`). | All |
| **Profile** | `/employee/addProfile` | `POST` | Creates a new employee profile and uploads the resume file (`multipart/form-data`). | AMC, BA |
| **BA/Mapping** | `/mapping/update` | `PUT` | Updates employee role, AMS name, and manager details. **(Requires BA Auth)** | BA |
| **BA/Interviews** | `/interviews` | `POST` | Schedules a new interview for an AMC. **(Requires BA Auth)** | BA |
| **BA/Interviews** | `/interviews/{id}` | `PUT` | Updates the result and feedback for a specific interview record. **(Requires BA Auth)** | BA |
| **AMS Portal** | `/ams/amc-details` | `GET` | Fetches the team list (AMCs) scoped to the authenticated AMS's project (`AMS_NAME`). **(Requires AMS Auth)** | AMS |
| **AMS Portal** | `/ams/amc/{id}/billable` | `PUT` | Updates the `isBillable` status for an AMC. **(Requires AMS Auth)** | AMS |
| **AMC Portal** | `/interviews/employee/{id}` | `GET` | Retrieves the full interview history for a specific employee. | AMC |

```
```
