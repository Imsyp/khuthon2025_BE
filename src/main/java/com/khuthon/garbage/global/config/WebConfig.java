package com.khuthon.garbage.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 명시적으로 허용할 도메인 추가
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")  // 프론트엔드 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*");
    }
}