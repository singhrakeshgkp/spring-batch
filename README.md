# Table of contents
- [Reference](#reference)
- [Tech Stack](#tech-stack)
- [Spring Batch Basic](#spring-batch-basic)
  - [DB Setup](#db-setup)
  - [Project Setup](#project-setup)

## Reference
- Josh Long Batch series YT video, Spring Batch Official Documentation..etc.
## Tech Stack
- Spring Boot
- Spring Batch
- PostGres DB

## Spring Batch Basic

### DB Setup
- Run ```docker compose up``` command from the dir where docker-compose.yml file is present.
- then  run ```docker exec -it <PSQL-Container-ID> bash``` command 
- finally run ```psql -h localhost -p 5432 -U postgres -W``` command to connect your terminal with postgres server.

## Project Setup
- Create new Spring boot project with following depedencies
   - Spring web
   - lombock
   - Graal VM
   - Spring Batch
   - Postgres sql
   - JDBC

- Specify the following properties in ```application.yml``` file.
 ``` yml
       spring:
    datasource:
      password: postgres
      username: postgres
      url: jdbc:postgresql://localhost/postgres
    batch:
      jdbc:
        initialize-schema: ALWAYS 
```
