package com.batch.ex.config;

import com.batch.ex.config.CSVToPostGresStepConfig;
import com.batch.ex.config.ErrorStepsConfig;
import com.batch.ex.constants.SpringBatchConstantsEx;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class SpringBatchJobConfig {
private final ErrorStepsConfig errorStepsConfig;
private final JobRepository jobRepository;
private final CSVToPostGresStepConfig csvToPostGresStepConfig;
private final EndStepConfig endStepConfig;

  @Bean
  Job job() {
    var readAndWriteBookStep = csvToPostGresStepConfig.readBookFromCsvAndWriteToDB();
    return new JobBuilder("job", jobRepository)
        .incrementer(new RunIdIncrementer())
        // .start(step) //if csv is corrupted or it contains 0 record call error step
        .start(readAndWriteBookStep)
        .on(SpringBatchConstantsEx.CORRUPTED_OR_EMPTY_CSV)
        .to(errorStepsConfig.errorStep())
        .from(readAndWriteBookStep).on("*").to(csvToPostGresStepConfig.bookAuthorAndSellerStep())
        //.next(csvToPostGresStepConfig.bookAuthorAndSellerStep())
        .next(endStepConfig.endStep())
        .build()
        .build();
  }
}
