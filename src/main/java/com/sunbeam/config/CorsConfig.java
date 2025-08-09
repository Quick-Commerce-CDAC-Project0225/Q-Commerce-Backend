package com.sunbeam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
	@Bean
	CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOriginPattern("*"); // Allow all origins
		config.addAllowedMethod("*"); // Allow all HTTP methods
		config.addAllowedHeader("*"); // Allow all headers
		config.setAllowCredentials(true); // Allow credentials (if using authentication cookies)

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}


}
