package com.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class SpringBatchExApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBatchExApplication.class, args);
  }

  record Book(int id, String title, String description, String author){

  }

  @Bean
  Step csvToPostgres(JobRepository jobRepository,PlatformTransactionManager txm,
      FlatFileItemReader<Book> csvFlatFileReader,JdbcBatchItemWriter csvRowJdbcItemWriter

  ) throws  Exception{

    return new StepBuilder("csvToPostgres", jobRepository)
        .<Book, Book>chunk(2, txm)
        .reader(csvFlatFileReader)
        .writer(csvRowJdbcItemWriter) // some writer
        .build();
  }


  @Bean
  ApplicationRunner runner(JobLauncher jobLauncher, Job job){
    return args -> {
      var jobParameters =
          new JobParametersBuilder()
              .addDate("date", new Date())//run job multiple times as by the time we run the application time will change
              .toJobParameters();
      var run = jobLauncher.run(job, jobParameters);
      var instanceId = run.getJobInstance().getInstanceId();
      System.out.println("instance id is : "+instanceId);
    };
  }
  @Bean
  @StepScope
  Tasklet tasklet(@Value("#{jobParameters['date']}") String date){
    return (contribution, chunkContext) -> {
      System.out.println("Hello world date = "+date);
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  Job job(JobRepository jobRepository, Step step,Step csvToPostgres) {
    return new JobBuilder("job", jobRepository)
        .start(step)
        .next(csvToPostgres)
        .build();
  }

  @Bean
  Step step(
      JobRepository jobRepository,
      Tasklet tasklet,
      PlatformTransactionManager platformTransactionManager) {
    return new StepBuilder("step1", jobRepository)
        .tasklet(tasklet, platformTransactionManager)
        .build();
  }

  @Bean
  JdbcTemplate template(DataSource dataSource){
    return new JdbcTemplate(dataSource);
  }


  // Item reader and Item writer
  @Bean
  FlatFileItemReader <Book> csvFlatFileReader(
      @Value("file://${HOME}/workspace/proj/spring_batch_proj/spring-batch/spring-batch-ex/src/main/resources/books.csv")
      Resource resource){

    return  new FlatFileItemReaderBuilder<Book>()
        .resource(resource)
        .name("books.csv")
        .delimited()
        .delimiter(",")
        .names("id,title,description,author".split(","))
        .linesToSkip(1)
        .fieldSetMapper(
            fieldSet -> new Book(
                fieldSet.readInt(0),
                fieldSet.readRawString(1),
                fieldSet.readString(2),
                fieldSet.readRawString(3)))
        .build();
  }

  @Bean
  JdbcBatchItemWriter<Book> csvRowJdbcItemWriter(DataSource datasource){
    var sql =
        """ 
       insert into books(
       id ,
       title,
       description ,
       author)
       values (
       :id ,
       :title,
       :description ,
       :author
       )   
      """;
  return   new JdbcBatchItemWriterBuilder<Book>()
        .sql(sql)
        .dataSource(datasource)
        .itemSqlParameterSourceProvider(
            item -> {
              var map = new HashMap<String, Object>();
              map.putAll(
                  Map.of(
                      "id", item.id(),
                      "title", item.title(),
                      "description", item.description(),
                      "author", item.author()));
              return new MapSqlParameterSource(map);
            })
        .build();
  }
}

