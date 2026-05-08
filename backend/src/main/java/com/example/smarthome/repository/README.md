[Return to Project Overview](../../../../../../../../README.md)

# Repository Pattern

***

The Repository Pattern is used to abstract data access logic and provide a clean separation between the application’s business logic and persistence layer.

Instead of interacting directly with the database or ORM, the system communicates through repository interfaces that handle all data operations.

Repositories act as an abstraction layer over the ORM, which handles persistence to the underlying database.

My implementation of the Repository Pattern can be found in the `/reposiotry` directory

---

## Purpose

The repository acts as a mediator between the domain layer and the data source, ensuring that business logic does not depend on database-specific implementations.

This prevents SQL queries and persistence logic from being spread across the codebase.

---

## Responsibilities

The repository layer is responsible for:

- Saving entities (e.g., smart devices, device logs)
- Retrieving stored data
- Updating existing records
- Deleting records
- Abstracting ORM/database interactions

---

## How It Works

1. The service layer requests data operations (e.g., save device)
2. The repository handles the request using ORM/database logic
3. The repository returns domain objects back to the service layer

The service layer never directly interacts with SQL or database queries.

---

## Core Structure

- **DeviceService**
    - Handles business logic for smart devices
    - Calls the repository to persist and retrieve data
    - Does not interact directly with the database or ORM

- **DeviceRepository (Interface)**
    - Defines the contract for data access operations
    - Provides standard CRUD-like methods such as save, find, update, and delete
    - Abstracts persistence details from the service layer

- **ORM / Database**
    - Handles the actual persistence of entities
    - Translates objects into database records and vice versa

## Example Repository Interface

DeviceRepository
- save(device)
- findById(id)
- findAll()
- deleteById(id)

## Benefits

- Clean separation between business logic and data access
- Reduced coupling between application layers
- Easier to test services using mocked repositories
- Simplifies changes to the underlying database or ORM