package com.equidad.firmaservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignioConfig {

    @Value("${signio.url}")
    private String url;

    @Value("${signio.token}")
    private String token;

    @Value("${signio.webhook-secret:}")
    private String webhookSecret;

    @Value("${signio.webhook-url:}")
    private String webhookUrl;

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }
}
