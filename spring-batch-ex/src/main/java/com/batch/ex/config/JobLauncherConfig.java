package com.batch.ex.config;

import java.util.Date;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobLauncherConfig {
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
}
