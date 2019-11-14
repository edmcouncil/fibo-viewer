package org.edmcouncil.spec.fibo.view;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@SpringBootApplication
@ComponentScan(basePackages = { 
  "org.edmcouncil.spec.fibo.view", 
  "org.edmcouncil.spec.fibo.weasel",
  "org.edmcouncil.spec.fibo.config"
})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

