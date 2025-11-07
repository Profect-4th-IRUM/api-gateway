package com.irum.apigateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties(prefix = "security.jwt")
@RefreshScope
public record JwtProperties(String accessTokenSecret) {}
