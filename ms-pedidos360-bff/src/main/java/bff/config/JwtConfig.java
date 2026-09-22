package bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class JwtConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${cognito.client-id}")
    private String expectedClientId;

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        // Validador de firma, expiración e Issuer
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);

        // Validador de Audience / ClientId para Cognito
        OAuth2TokenValidator<Jwt> clientIdValidator = token -> {
            String clientId = token.getClaimAsString("client_id");
            List<String> audience = token.getAudience();

            boolean isValid = (clientId != null && clientId.equals(expectedClientId)) ||
                    (audience != null && audience.contains(expectedClientId));

            if (isValid) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "El token no corresponde al Client ID esperado", null)
            );
        };

        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, clientIdValidator));
        return jwtDecoder;
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> cognitoJwtConverter() {
        return jwt -> {
            // Cognito inyecta los grupos/roles en "cognito:groups"
            Collection<String> groups = jwt.getClaimAsStringList("cognito:groups");
            if (groups == null) {
                groups = Collections.emptyList();
            }

            var authorities = groups.stream()
                    .map(group -> group.startsWith("ROLE_") ? group : "ROLE_" + group)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            String principalClaim = jwt.getClaimAsString("username") != null
                    ? jwt.getClaimAsString("username")
                    : jwt.getSubject();

            return new JwtAuthenticationToken(jwt, authorities, principalClaim);
        };
    }
}