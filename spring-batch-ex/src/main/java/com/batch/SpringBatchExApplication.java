package com.batch;

import java.io.InputStreamReader;
import java.util.Arrays;
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
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindException;

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
  Job job(JobRepository jobRepository, Step step,Step csvToDB) {
    return new JobBuilder("job", jobRepository)
        .start(step)
        .next(csvToDB)
        .build();
  }

  /*Variant 1 not recomended*/
  @Bean
  Step csvToPostgres(JobRepository jobRepository,PlatformTransactionManager platformTransactionManager,
      @Value("file://${HOME}/workspace/proj/spring_batch_proj/spring-batch/spring-batch-ex/src/main/resources/books.csv")Resource data) throws  Exception{

    var lines = (String[]) null;
    try(var reader = new InputStreamReader(data.getInputStream())){
      var str = FileCopyUtils.copyToString(reader);
      lines = str.split(System.lineSeparator());
      System.out.println("thre are "+lines.length +" rows");
    }
    return new StepBuilder("csvToDB", jobRepository)
        .<String, String>chunk(2, platformTransactionManager)
        .reader(new ListItemReader<>(Arrays.asList(lines)))
        .writer(
            new ItemWriter<String>() {
              @Override
              public void write(Chunk<? extends String> chunk) throws Exception {

                var twoRows = chunk.getItems();
                System.out.println(twoRows);
              }
            }) // some writer
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
}
