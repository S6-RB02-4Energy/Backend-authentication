package com.Energy.BasicSpringAPI;

import org.hibernate.HibernateException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class BasicSpringApiApplication {
	static final Logger logger = Logger.getLogger("com.Energy.AuthenticationAPI");


	public static void main(String[] args) {
		try{
			SpringApplication.run(BasicSpringApiApplication.class, args);
		}catch (HibernateException e){
			logger.log(Level.SEVERE, "DB error on startup, shutting down...");
			System.exit(0);
		}
	}

}
