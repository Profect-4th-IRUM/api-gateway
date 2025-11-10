package com.irum.apigateway.config;

import com.irum.apigateway.properties.GatewayPathProperties;
import com.irum.apigateway.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({JwtProperties.class, GatewayPathProperties.class})
@Configuration
public class PropertiesConfig {}
