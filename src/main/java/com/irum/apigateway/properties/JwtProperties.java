package com.irum.apigateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
// @RefreshScope
public record JwtProperties(String accessTokenSecret) {}
