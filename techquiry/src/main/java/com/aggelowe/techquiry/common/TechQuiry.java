package com.aggelowe.techquiry.common;

import static com.aggelowe.techquiry.common.Constants.LOGGER;
import static com.aggelowe.techquiry.common.Constants.NAME;
import static com.aggelowe.techquiry.common.Constants.VERSION;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.aggelowe.techquiry.database.DatabaseInitializer;

/**
 * This is the main class of the TechQuiry application, it is responsible for
 * starting the spring boot application by invoking the necessary methods when
 * the execution occurs.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@SpringBootApplication
public class TechQuiry {

	public static void main(String[] args) {
		LOGGER.info("Starting the " + NAME + " application on version " + VERSION);
		LOGGER.debug("Application work directory: " + Environment.getWorkDirectory());
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
		Map<String, Object> applicationProperties = new HashMap<>();
		applicationProperties.put("server.port", Environment.getPort());
		application.setDefaultProperties(applicationProperties);
	}

	/**
	 * This method is invoked when the application's context has been initialized
	 * and is responsible for initializing the core application components.
	 * 
	 * @param event The object representing the event
	 */
	@EventListener(ContextRefreshedEvent.class)
	public void start() {
		LOGGER.info("Starting core application components");
		DatabaseInitializer.initialize();
	}

}
