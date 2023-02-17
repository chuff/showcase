# dataservice

Microservice Template

## Technologies
- Java 17
- Maven
- Spring Boot
- gRPC
- REST
- JPA
- MySQL
- H2
- Docker

## Running In Docker
> make run 

This will build and launch the application configured to run along side a MySQL database docker container and a Redis cache docker container.

## Running Locally
> mvn clean install && cd dataservice-app && mvn spring-boot:run 

This will build and launch the application configured to run with an in-memory H2 database and an in-memory Caffeine cache.

## Run Test Suite
> make test 

This runs a suite of functional tests against the gRPC and REST endpoints
