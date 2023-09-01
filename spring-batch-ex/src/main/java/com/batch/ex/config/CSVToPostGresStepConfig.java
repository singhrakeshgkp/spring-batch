package com.batch.ex.config;

import com.batch.ex.constants.SpringBatchConstantsEx;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class CSVToPostGresStepConfig {

  private  final DataSource dataSource;
  private  final Resource resource;
  private final JobRepository jobRepository;
  private  final PlatformTransactionManager txm;
  private  final TransactionTemplate tx;
  private final JdbcTemplate jdbcTemplate;
  public CSVToPostGresStepConfig(
      DataSource dataSource,
      @Value("${csvFilePath}") Resource resource,
      JobRepository jobRepository,
      PlatformTransactionManager txm,
      TransactionTemplate tx,
      JdbcTemplate jdbcTemplate) {

    this.dataSource = dataSource;
    this.resource = resource;
    this.jobRepository = jobRepository;
    this.txm = txm;
    this.tx = tx;
    this.jdbcTemplate = jdbcTemplate;
  }

  record Book(int id, String title, String description, String author,String sellerName){

  }

  record Author(int id,String name){

  }

  record seller(int id, String name){

  }

  @Bean
  FlatFileItemReader<Book> csvFlatFileReader(){

    return  new FlatFileItemReaderBuilder<Book>()
        .resource(resource)
        .name("books.csv")
        .delimited()
        .delimiter(",")
        .names("id,title,description,author,seller".split(","))
        .linesToSkip(1)
        .fieldSetMapper(
            fieldSet -> new Book(
                fieldSet.readInt(0),
                fieldSet.readRawString(1),
                fieldSet.readString(2),
                fieldSet.readRawString(3),
                fieldSet.readRawString(4)))
        .build();
  }


  @Bean
  JdbcBatchItemWriter<Book> csvRowJdbcItemWriter(){
    var sql =
        """ 
       insert into books(
       id ,
       title,
       description ,
       author,
       seller
       )
       values (
       :id ,
       :title,
       :description ,
       :author,
       :seller
       ) 
       ON CONFLICT (id) DO UPDATE
       SET
       id=excluded.id,
       title=excluded.title,
       description=excluded.description,
       author = excluded.author,
       seller = excluded.seller
       
      """;
    return   new JdbcBatchItemWriterBuilder<Book>()
        .sql(sql)
        .dataSource(dataSource)
        .itemSqlParameterSourceProvider(
            item -> {
              var map = new HashMap<String, Object>();
              System.out.println(" Author "+item.author());
              map.putAll(
                  Map.of(
                      "id", item.id(),
                      "title", item.title(),
                      "description", item.description(),
                      "author", item.author(),
                       "seller",item.sellerName));
              return new MapSqlParameterSource(map);
            })
        .build();
  }
  @Bean
  Step step(
      JobRepository jobRepository,
      Tasklet tasklet,
      PlatformTransactionManager platformTransactionManager) {
    return new StepBuilder("step", jobRepository)
        .tasklet(tasklet, platformTransactionManager)
        .build();
  }

  @Bean
  Step readBookFromCsvAndWriteToDB(){

    return new StepBuilder("readBookFromCsvAndWriteToDB", jobRepository)
        .<Book, Book>chunk(2, txm)
        .reader(csvFlatFileReader())
        .writer(csvRowJdbcItemWriter())
        .listener(new StepExecutionListener() {
          @Override
          public ExitStatus afterStep(StepExecution stepExecution) {
            var count = Objects.requireNonNull(
                jdbcTemplate.queryForObject("select coalesce(count(*),0) from books", Integer.class));
            var status = count == 0? new ExitStatus(SpringBatchConstantsEx.CORRUPTED_OR_EMPTY_CSV):ExitStatus.COMPLETED;
            return status;
          }
        })
        .build();
  }

  @Bean
  Step bookAuthorAndSellerStep() {
    return new StepBuilder("bookAuthorStep", jobRepository)
        .tasklet(
            (Tasklet)
                (contribution, chunkContext) ->
                    tx.execute(
                        (TransactionCallback<RepeatStatus>)
                            status -> {
                              jdbcTemplate.execute(
                                  """
                      insert into authors (name)
                      select author from books
                      on conflict (name) do nothing;
                      """);
                              jdbcTemplate.execute(
                                  """
                      insert into seller (name)
                      select seller from books
                      on conflict (name)
                      do nothing;
                      
                        """);
                              return RepeatStatus.FINISHED;
                            }),
            txm)
        .build();
  }
}
