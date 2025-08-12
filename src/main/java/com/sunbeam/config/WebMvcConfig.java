package com.sunbeam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Serve /images/** from the container's filesystem folder where you save files
    registry.addResourceHandler("/images/**")
            .addResourceLocations("file:/app/src/main/resources/static/images/"); // <- trailing slash matters
  }
}
