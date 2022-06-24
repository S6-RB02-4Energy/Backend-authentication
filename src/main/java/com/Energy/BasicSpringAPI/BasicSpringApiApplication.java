package com.Energy.BasicSpringAPI;

import io.github.cdimascio.dotenv.Dotenv;
import org.hibernate.HibernateException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main Class
 *
 * @class BasicSpringApiApplication
 */
@SpringBootApplication
public class BasicSpringApiApplication {
	//For logging changes properly
	static final Logger logger = Logger.getLogger("com.Energy.AuthenticationAPI");


	public static void main(String[] args) {
		try {
			SpringApplication.run(BasicSpringApiApplication.class, args);
		} catch (HibernateException e){
			//Logging the error and stopping the application
			logger.log(Level.SEVERE, "DB error on startup, shutting down...");
			System.exit(0);
		}
	}

}
