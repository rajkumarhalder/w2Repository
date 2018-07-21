package com.w2meter.W2meterService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan("com.w2meter.")
@EnableAutoConfiguration
@EnableJpaRepositories("com.w2meter.repository")
@EntityScan("com.w2meter.entity")   
public class W2meterServiceApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(W2meterServiceApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(W2meterServiceApplication.class);
	}
}
