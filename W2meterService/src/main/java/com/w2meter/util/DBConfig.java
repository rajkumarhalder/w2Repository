package com.w2meter.util;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DBConfig {

	@Bean
    public DataSource dataSource(){
       DriverManagerDataSource dataSource = new DriverManagerDataSource();
       dataSource.setDriverClassName("com.mysql.jdbc.Driver");
       dataSource.setUrl("jdbc:mysql://178.128.252.211:3306/test");
       dataSource.setUsername("w2meter");
       dataSource.setPassword("admin@1234" );
       return dataSource;
    }
}
