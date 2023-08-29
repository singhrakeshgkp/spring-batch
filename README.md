# Table of contents
- [Reference](#reference)
- [Tech Stack](#tech-stack)
- [Spring Batch Basic](#spring-batch-basic)
  - [DB Setup](#db-setup)
  - [Project Setup](#project-setup)
  - [001 Spring Batch Hello world](#001-spring-batch-hello-world)
  - [002 Spring Batch Hello world](#002-spring-batch-hello-world)
  - [003 Spring Batch Hello world](#003-spring-batch-hello-world)
  - [004 SB Persist data](#004-sb-prsist-data)
  - [005 SB Persist data](#005-sb-persist-data)


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

##  002 Spring Batch Hello world

<p>Run the job each time we start the application</p>

- Make the following changes to run the job each time we start our application.
```java
 @Bean
  ApplicationRunner runner(JobLauncher jobLauncher, Job job){
    return new ApplicationRunner() {
      @Override
      public void run(ApplicationArguments args) throws Exception {
        var jobParameters =
            new JobParametersBuilder()
                .addString("uniqueId", UUID.randomUUID().toString())
                .toJobParameters();
        var run = jobLauncher.run(job, jobParameters);
        var instanceId = run.getJobInstance().getInstanceId();
        System.out.println("instance id is : "+instanceId);
      }
    };
  }
```  
- Make the following changes in order to include the unique generated id in the console message. ```Tasklet``` Bean gets created everyt time we run the job.
```java
  @Bean
  @StepScope
  Tasklet tasklet(@Value("#{jobParameters['uniqueId']}") String uniqueId){
    return (contribution, chunkContext) -> {
      System.out.println("Hello world uniqueId = "+uniqueId);
      return RepeatStatus.FINISHED;
    };
  }
```

## 003 Spring Batch Hello world
<p>Running job on a given duration for example dailly once</p>

- Running job completely depends on the job parameter if job parameter is unique, job will run each time, if not it will end up with message ```Job instance already exist```

## 004 SB Persist data
<p>Here we will follow one approach to read the data from csv but this approach is not recommended way. For this create a bean ```csvToPostgres``` and then run the program, you should be able to see the file content sonsole.</p>


## 005 SB Persist data
