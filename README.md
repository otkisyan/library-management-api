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