package com.rookie.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.rookie.*"})
public class RookieWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(RookieWebApplication.class, args);
	}

}
