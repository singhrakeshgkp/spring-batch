package com.batch;

import com.batch.SpringBatchExApplication.Book;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class BookItemReader{
  @Bean
  FlatFileItemReader<Book> csvFlatFileReader(
      @Value("file://${HOME}/workspace/proj/spring_batch_proj/spring-batch/spring-batch-ex/src/main/resources/books.csv")
      Resource resource){

    return  new FlatFileItemReaderBuilder<Book>()
        .resource(resource)
        .name("books.csv")
        .delimited()
        .delimiter(",")
        .names("id,title,description,author".split(","))
        .linesToSkip(1)
        .fieldSetMapper(
            fieldSet -> new Book(
                fieldSet.readInt(0),
                fieldSet.readRawString(1),
                fieldSet.readString(2),
                fieldSet.readRawString(3)))
        .build();
  }
}
