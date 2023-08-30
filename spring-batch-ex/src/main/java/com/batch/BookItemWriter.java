package com.batch;

import com.batch.SpringBatchExApplication.Book;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

@Configuration
public class BookItemWriter {
  @Bean
  JdbcBatchItemWriter<Book> csvRowJdbcItemWriter(DataSource datasource){
    var sql =
        """ 
       insert into books(
       id ,
       title,
       description ,
       author)
       values (
       :id ,
       :title,
       :description ,
       :author
       )   
      """;
    return   new JdbcBatchItemWriterBuilder<Book>()
        .sql(sql)
        .dataSource(datasource)
        .itemSqlParameterSourceProvider(
            item -> {
              var map = new HashMap<String, Object>();
              System.out.println(" Author "+item.author());
              map.putAll(
                  Map.of(
                      "id", item.id(),
                      "title", item.title(),
                      "description", item.description(),
                      "author", item.author()));
              return new MapSqlParameterSource(map);
            })
        .build();
  }
}
