package de.instinct.eqfleetgameserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.instinct.eqspringutils.loggerinterceptor.RequestLoggerInterceptor;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	@Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLoggerInterceptor());
    }

}
