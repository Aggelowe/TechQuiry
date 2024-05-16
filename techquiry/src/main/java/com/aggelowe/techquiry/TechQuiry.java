package com.aggelowe.techquiry;

import static com.aggelowe.techquiry.ApplicationReference.LOGGER;
import static com.aggelowe.techquiry.ApplicationReference.NAME;
import static com.aggelowe.techquiry.ApplicationReference.VERSION;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the main class of the TechQuiry application, it is responsible for
 * starting the spring boot application by invoking the necessary methods when
 * the execution occurs.
 * 
 * @author Angelos Margaritis (Aggelowe)
 * @since 0.0.1
 */
@SpringBootApplication
public class TechQuiry {

	public static void main(String[] args) {
		LOGGER.info("Starting the " + NAME + " application on version " + VERSION + "...");
		SpringApplication application = new SpringApplication(TechQuiry.class);
		application.run();
	}

}
