package com.aggelowe.techquiry.common;

import static com.aggelowe.techquiry.common.Constants.NAME;
import static com.aggelowe.techquiry.common.Constants.VERSION;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.aggelowe.techquiry.database.DatabaseManager;

import lombok.extern.log4j.Log4j2;

/**
 * This is the main class of the TechQuiry application, it is responsible for
 * starting the spring boot application by invoking the necessary methods when
 * the execution occurs.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Log4j2
@SpringBootApplication
@Import({ AppConfiguration.class })
public class TechQuiry {

	@Autowired
	private DatabaseManager databaseManager;

	public static void main(String[] args) {
		log.info("Starting the " + NAME + " application on version " + VERSION);
		log.debug("Application work directory: " + Environment.WORK_DIRECTORY);
		SpringApplication application = new SpringApplication(TechQuiry.class);
		properties(application);
		log.info("Invoking Spring application startup");
		application.run();
	}

	/**
	 * This method is responsible for applying specific properties to the given
	 * {@link SpringApplication} object before the execution.
	 * 
	 * @param application The object representing the spring application
	 */
	private static void properties(SpringApplication application) {
		log.debug("Setting up Spring application properties");
		Map<String, Object> applicationProperties = new HashMap<>();
		applicationProperties.put("server.port", Environment.PORT);
		application.setDefaultProperties(applicationProperties);
	}

	/**
	 * This method is invoked when the application's context has been initialized
	 * and is responsible for initializing the core application components.
	 */
	@EventListener(ContextRefreshedEvent.class)
	void start() {
		log.info("Starting core application components");
		databaseManager.initialize();
	}

	/**
	 * This method is invoked when the application's context is closed and is
	 * responsible for cleaning up core application components.
	 */
	@EventListener(ContextClosedEvent.class)
	void shutdown() {
		log.info("Shutting down core application components");
	}

}
