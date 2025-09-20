package org.garlikoff.restdata.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class OAuth2ResourceServerConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public JwtDecoder jwtDecoder() {
        if (issuerUri != null) {
            return JwtDecoders.fromIssuerLocation(issuerUri);
        } else if (jwkSetUri != null) {
            return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        }
        throw new IllegalStateException("Neither issuer-uri nor jwk-set-uri is configured");
    }
}