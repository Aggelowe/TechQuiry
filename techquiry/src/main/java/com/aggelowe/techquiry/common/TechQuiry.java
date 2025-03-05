package com.aggelowe.techquiry.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.aggelowe.techquiry.database.DatabaseManager;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TechQuiry {

	/**
	 * The manager responsible for initializing the database.
	 */
	private final DatabaseManager databaseManager;

	public static void main(String[] args) {
		log.info("Starting %s v%s".formatted(Constants.APPLICATION_NAME, Constants.APPLICATION_VERSION));
		log.debug("Application work directory: %s".formatted(Environment.SERVER_WORK_DIRECTORY));
		SpringApplication application = new SpringApplication(TechQuiry.class);
		properties(application);
		log.info("Invoking Spring application startup");
		application.run();
	}

	/**
	 * This method applies specific properties to the given
	 * {@link SpringApplication} object.
	 * 
	 * @param application The object of the spring application
	 */
	private static void properties(SpringApplication application) {
		log.debug("Setting up Spring application properties");
		Map<String, Object> applicationProperties = new HashMap<>();
		applicationProperties.put("server.port", Environment.SERVER_PORT);
		applicationProperties.put("springdoc.api-docs.enabled", Environment.DOCS_SWAGGER);
		applicationProperties.put("springdoc.swagger-ui.enabled", Environment.DOCS_SWAGGER);
		application.setDefaultProperties(applicationProperties);
	}

	/**
	 * This method initializes the core application components when the
	 * application's context has been initialized.
	 */
	@EventListener(ContextRefreshedEvent.class)
	void start() {
		log.info("Starting core application components");
		databaseManager.initialize();
	}

	/**
	 * This method cleans up core application components invoked when the
	 * application's context is closed.
	 */
	@EventListener(ContextClosedEvent.class)
	void shutdown() {
		log.info("Shutting down core application components");
	}

}
