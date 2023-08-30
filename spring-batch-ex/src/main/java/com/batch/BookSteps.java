package com.batch;

import com.batch.SpringBatchExApplication.Book;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BookSteps {

  /*Define step beans*/
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
  Step csvToPostgres(JobRepository jobRepository,PlatformTransactionManager txm,
      FlatFileItemReader<Book> csvFlatFileReader, JdbcBatchItemWriter csvRowJdbcItemWriter

  ) throws  Exception{

    return new StepBuilder("csvToPostgres", jobRepository)
        .<Book, Book>chunk(2, txm)
        .reader(csvFlatFileReader)
        .writer(csvRowJdbcItemWriter) // some writer
        .build();
  }
}
