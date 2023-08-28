# Table of contents
- [Tech Stack](#tech-stack)
- [Spring Batch Basic](#spring-batch-basic)
  - [DB Setup](#db-setup)
  - [Project Setup](#project-setup)


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


