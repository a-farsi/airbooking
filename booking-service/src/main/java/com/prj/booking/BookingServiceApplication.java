package com.prj.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookingServiceApplication implements CommandLineRunner {

	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	public static void main(String[] args) {
		SpringApplication.run(BookingServiceApplication.class, args);
	}

	@Override
	public void run(String... args) {
		System.out.println(" >> Effective DB URL: " + datasourceUrl);
	}

}
