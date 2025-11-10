package com.irum.apigateway.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpMethod;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "security.endpoints")
public class GatewayPathProperties {

    private List<Route> publicEndpoints;
    private List<Route> customerEndpoints;
    private List<Route> ownerEndpoints;
    private List<Route> managerEndpoints;
    private List<Route> masterEndpoints;

    @Data
    public static class Route {
        private String path;
        private List<HttpMethod> methods;
    }
}
