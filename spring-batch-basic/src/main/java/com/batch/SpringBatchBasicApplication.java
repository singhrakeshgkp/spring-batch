package com.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class SpringBatchBasicApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchBasicApplication.class, args);
	}

  @Bean
  Tasklet tasklet(){
    return (contribution, chunkContext) -> {
      System.out.println("Hello world");
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  Job job(JobRepository jobRepository, Step step){
    return  new JobBuilder("job",jobRepository).start(step).build();
  }

  @Bean
  Step step1(JobRepository jobRepository, Tasklet tasklet, PlatformTransactionManager platformTransactionManager){
    return  new StepBuilder("step1", jobRepository).tasklet(tasklet,platformTransactionManager).build();
  }

}
