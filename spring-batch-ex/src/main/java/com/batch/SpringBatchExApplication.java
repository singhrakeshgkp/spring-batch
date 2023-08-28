package com.batch;

import java.util.UUID;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class SpringBatchExApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchExApplication.class, args);
	}

  @Bean
  ApplicationRunner runner(JobLauncher jobLauncher, Job job){
    return args -> {
      var jobParameters =
          new JobParametersBuilder()
              .addString("uniqueId", UUID.randomUUID().toString())
              .toJobParameters();
      var run = jobLauncher.run(job, jobParameters);
      var instanceId = run.getJobInstance().getInstanceId();
      System.out.println("instance id is : "+instanceId);
    };
  }
  @Bean
  @StepScope
  Tasklet tasklet(@Value("#{jobParameters['uniqueId']}") String uniqueId){
    return (contribution, chunkContext) -> {
      System.out.println("Hello world uniqueId = "+uniqueId);
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("job", jobRepository).start(step).build();
  }

  @Bean
  Step step1(
      JobRepository jobRepository,
      Tasklet tasklet,
      PlatformTransactionManager platformTransactionManager) {
    return new StepBuilder("step1", jobRepository)
        .tasklet(tasklet, platformTransactionManager)
        .build();
  }
}
