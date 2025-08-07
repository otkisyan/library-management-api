<h1 align="center">
Library Management API
</h1>
<div align="center">

[![ci](https://github.com/otkisyan/library-management-api/actions/workflows/ci.yml/badge.svg)](https://github.com/otkisyan/library-management-api/actions/workflows/ci.yml)

</div>
<p align="center">
A robust, scalable microservices platform designed for library management. Book cataloguing with item-based collaborative filtering for book recommendations, real-time review aggregation, secure user management.  
</p>

## Overview
This repository hosts a library management microservices application built with Spring Boot and Java. The system comprises the following main components:
- book-service: Manages book entities (CRUD operations and book similarity recommendations using item-based collaborative filtering using cosine similarity)
- review-service: Handles book reviews and updates average ratings by sending Kafka events
- user-service: Integrates with Keycloak for user provisioning and role management
- eureka: Service registry using Spring Cloud Netflix Eureka
- gateway: API Gateway using Spring Cloud Gateway

## Stack

- Java 
- Frameworks: Spring Boot, Spring Cloud (Gateway, Eureka), Spring Security, Spring Web, Spring Data JPA
- Authentication & Authorization: Keycloak, OAuth2, OpenID Connect
- Messaging: Apache Kafka
- Service Discovery: Spring Cloud Netflix Eureka
- Containerization: Docker, Docker Compose
- Databases: MariaDB (for book-service and review-service) & PostgreSQL for Keycloak
- Testing: JUnit, Mockito, Testcontainers


## Getting Started

### Prerequisites

- JDK 17+
- Maven
- Docker and Docker Compose

### Installation

**Before you start:** Change environment variable values in `.env` file for more security or leave it as it is.

1. Clone the repo

```bash
> git clone https://github.com/otkisyan/library-management-api.git
> cd library-management-api
```
2. Compile and package the code

```bash
> mvn clean package
```

3. Run the Application

```bash
> docker-compose up -d 
```

4. Configure Keycloak’s hostname resolution in your development environment.

*/etc/hosts*
```bash
127.0.0.1       keycloak
```
Alternatively, adjust the Keycloak container settings in `docker-compose.yml` or modify the Keycloak-related properties in each microservice’s `application-dev.yaml` to match your local setup.

This is important for the proper generation of URLs by the Keycloak server, such as the `iss` field in the issued tokens, which must match the hostname the services use to validate tokens.

### Endpoints:

- http://localhost:3308 — MariaDB (user: `root`, password: `$MARIADB_ROOT_PASSWORD`, database: `$MARIADB_DATABASE`)
- http://localhost:9094 — Kafka
- http://localhost:8090 — Kafka UI
- http://localhost:5432 — PostgreSQL (database: `$POSTGRESQL_DATABASE`, user: `$POSTGRESQL_USERNAME`, password: `$POSTGRESQL_PASSWORD`)
- http://localhost:8089 — Keycloak (user: `$KEYCLOAK_ADMIN`, password: `$KEYCLOAK_ADMIN_PASSWORD`)
- http://localhost:8761 — Eureka
- http://localhost:8085 — Gateway Service
- http://localhost:8082 — Book Service
- http://localhost:8083 — Review Service
- http://localhost:8088 — User Service  
