package com.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBatchJobConfig {

  @Bean
  Job job(JobRepository jobRepository, Step step,Step csvToPostgres) {
    return new JobBuilder("job", jobRepository)
        .start(step)
        .next(csvToPostgres)
        .build();
  }
}
