package com.batch.ex;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class SpringBatchExApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBatchExApplication.class, args);
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
  JdbcTemplate template(DataSource dataSource){
    return new JdbcTemplate(dataSource);
  }
}

