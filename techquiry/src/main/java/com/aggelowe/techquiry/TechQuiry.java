package com.aggelowe.techquiry;

import static com.aggelowe.techquiry.Reference.EXECUTION_DIRECTORY;
import static com.aggelowe.techquiry.Reference.LOGGER;
import static com.aggelowe.techquiry.Reference.NAME;
import static com.aggelowe.techquiry.Reference.VERSION;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.aggelowe.techquiry.database.DatabaseController;

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
		Map<String, Object> applicationProperties = new HashMap<>();
		applicationProperties.put("server.port", Configuration.getPort());
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
		DatabaseController.initialize();
	}

}
