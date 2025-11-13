package com.irum.apigateway.filter;

import com.irum.apigateway.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtProperties jwtProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        try {
            SecretKey key =
                    Keys.hmacShaKeyFor(
                            jwtProperties.accessTokenSecret().getBytes(StandardCharsets.UTF_8));
            Claims claims =
                    Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            String memberId = claims.getSubject();
            String role = claims.get("authority", String.class);
            GrantedAuthority authority = new SimpleGrantedAuthority(role);
            Collection<GrantedAuthority> authorities = Collections.singletonList(authority);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(memberId, null, authorities);
            ServerHttpRequest mutatedRequest =
                    exchange.getRequest().mutate().header("X-Member-Id", memberId).build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

            return chain.filter(mutatedExchange)
                    .contextWrite(
                            ReactiveSecurityContextHolder.withSecurityContext(
                                    Mono.just(new SecurityContextImpl(auth))));

        } catch (JwtException e) {
            log.warn("JWT Validation failed: {}", e.getMessage());
            return chain.filter(exchange);
        }
    }
}
