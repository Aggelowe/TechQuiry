package com.aggelowe.techquiry;

import static com.aggelowe.techquiry.ApplicationReference.LOGGER;
import static com.aggelowe.techquiry.ApplicationReference.NAME;
import static com.aggelowe.techquiry.ApplicationReference.VERSION;
import static com.aggelowe.techquiry.ApplicationReference.EXECUTION_DIRECTORY;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.aggelowe.techquiry.config.Configuration;

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
		LOGGER.info("Starting the " + NAME + " application on version " + VERSION);
		LOGGER.debug("Application execution directory: " + EXECUTION_DIRECTORY);
		Configuration.initialize();
		SpringApplication application = new SpringApplication(TechQuiry.class);
		setup(application);
		LOGGER.info("Invoking Spring application startup");
		application.run();
	}

	/**
	 * This method is responsible for applying certain changes and options to the
	 * given {@link SpringApplication} object before the execution.
	 * 
	 * @param application The object representing the spring application
	 */
	private static void setup(SpringApplication application) {
		LOGGER.debug("Setting up Spring application properties");
	}

}
