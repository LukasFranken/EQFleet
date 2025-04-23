package de.instinct.eqfleetgameserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
public class ApplicationConfig {

	@Value("${application.version:missing_version}")
	private String version;
	
}