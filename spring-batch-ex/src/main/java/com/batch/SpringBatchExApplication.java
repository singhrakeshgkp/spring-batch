package com.batch;

import java.util.Date;
import java.util.UUID;
import javax.sql.DataSource;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class SpringBatchExApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchExApplication.class, args);
	}

  @Bean
  ApplicationRunner runner(JobLauncher jobLauncher, Job job){
    return args -> {
      var today = new Date();
      var dateStr = today.getDay()+"_" + today.getMonth()+"_"+today.getYear();
      var jobParameters =
          new JobParametersBuilder()
              //.addDate("date", today)//run job multiple times as by the time we run the application time will change
              .addString("dateStr",dateStr)// run once a day
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
  Job job(JobRepository jobRepository, Step step) {
    return new JobBuilder("job", jobRepository).start(step).build();
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
}
