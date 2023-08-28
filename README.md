# Table of contents
- [Reference](#reference)
- [Tech Stack](#tech-stack)
- [Spring Batch Basic](#spring-batch-basic)
  - [DB Setup](#db-setup)
  - [Project Setup](#project-setup)
  - [001 Spring Batch Hello world](#001-spring-batch-hello-world)

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
- Now run the application and go to postgress cli and check the schema using command ```\d``` command, schema shema should be created.

## 001 Spring Batch Hello world

### Spring Batch Default Behaviour 

<p>
 
  By default spring batch run the job automatically for us, it keep track of the job in the generated tables, for example ```batch_job_execution``` ... etc

</p>

- Go to ```SpringBatchExApplication``` and define ```Tasklet, Job and Step``` bean respectively.  In tasklet bean print ```Hello World```
- Run the application multiple times, hello world will be printed first time only rest of the time it will not print as task has already been executed.

### Override the default behaviour
- To instruct the spring not to run the job automatically use below configuration in application.yml file.
  ```
  spring.batch.job.enabled=false
  ```
- Now run the application and observe the console and job execution table, you will observe the job would not run.
