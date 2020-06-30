package jb.dam2.discover;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
public class DiscoverApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(DiscoverApplication.class, args);
	}
	
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(DiscoverApplication.class);
	}
	
	@PostConstruct
    void started() {
        // set JVM timezone as CET
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
    }

}
