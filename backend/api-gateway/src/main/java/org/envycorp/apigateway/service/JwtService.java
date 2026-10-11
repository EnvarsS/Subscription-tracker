package org.envycorp.apigateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtService {
    private final JwtDecoder jwtDecoder;

    public JwtService(@Value("${keycloak.jwk-set-uri}") String jwkSetUri,
                      @Value("${keycloak.issuer-uri}") String issuerUri,
                      @Value("${keycloak.issuer-uri-local}") String issuerUriLocal) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        OAuth2TokenValidator<Jwt> withTimestamp = JwtValidators.createDefault();
        OAuth2TokenValidator<Jwt> withIssuer = new JwtClaimValidator<String>(
                "iss", iss -> List.of(
                        issuerUri,
                        issuerUriLocal
                ).contains(iss)
        );

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withTimestamp, withIssuer));
        this.jwtDecoder = decoder;
    }

    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }
}
