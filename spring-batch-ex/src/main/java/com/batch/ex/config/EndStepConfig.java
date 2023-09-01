package com.batch.ex.config;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class EndStepConfig {
  private  final JobRepository jobRepository;
  private final PlatformTransactionManager txm;

  Step endStep() {
   return new StepBuilder("endStep", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              System.out.println("task finished.....");
              return RepeatStatus.FINISHED;
            },
            txm)
        .build();
  }
}
