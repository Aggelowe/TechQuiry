package com.aggelowe.techquiry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the main class of the TechQuiry application, it is responsible for
 * starting the spring boot application by invoking the necessary methods when
 * the execution occurs.
 * 
 * @author Angelos Margaritis (Aggelowe)
 * @since 0.0.1 Snapshot
 */
@SpringBootApplication
public class TechQuiry {

	public static void main(String[] args) {
		SpringApplication.run(TechQuiry.class, args);
	}

}
