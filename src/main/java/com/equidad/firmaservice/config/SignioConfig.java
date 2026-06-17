package com.equidad.firmaservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignioConfig {

    @Value("${signio.url}")
    private String url;

    @Value("${signio.token}")
    private String token;

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }
}