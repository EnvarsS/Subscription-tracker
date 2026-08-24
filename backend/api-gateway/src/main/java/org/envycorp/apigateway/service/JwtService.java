package org.envycorp.apigateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtDecoder jwtDecoder;

    public JwtService(@Value("${keycloak.issuer-uri}") String issuerUri) {
        this.jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
    }

    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }
}
