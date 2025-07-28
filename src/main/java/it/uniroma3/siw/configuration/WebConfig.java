package it.uniroma3.siw.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UploadProperties uploadProperties;

    @SuppressWarnings("null")
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadProperties.getBaseDir() + "/");
    }

    @PostConstruct
    public void checkUploadDir() {
        System.out.println("Upload base dir: " + uploadProperties.getBaseDir());
    }
}