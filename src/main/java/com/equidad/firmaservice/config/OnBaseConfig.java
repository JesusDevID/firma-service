package com.equidad.firmaservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OnBaseConfig {

    @Value("${onbase.url:}")
    private String url;

    @Value("${onbase.token:}")
    private String token;

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }
}
