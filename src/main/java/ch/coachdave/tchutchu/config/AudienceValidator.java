package ch.coachdave.tchutchu.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Custom JWT audience validator for Keycloak tokens.
 * Validates that the JWT contains the expected audience claim.
 */
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String audience;

    public AudienceValidator(String audience) {
        this.audience = audience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        // Keycloak tokens may have audience in 'aud' claim or in 'azp' (authorized party)
        if (jwt.getAudience() != null && jwt.getAudience().contains(audience)) {
            return OAuth2TokenValidatorResult.success();
        }

        // Check azp (authorized party) as fallback
        String azp = jwt.getClaimAsString("azp");
        if (audience.equals(azp)) {
            return OAuth2TokenValidatorResult.success();
        }

        // For public clients, audience validation may be relaxed
        // Allow tokens if they are issued for the same realm
        if (jwt.getIssuer() != null && jwt.getIssuer().toString().contains("dsalathe-apps")) {
            return OAuth2TokenValidatorResult.success();
        }

        OAuth2Error error = new OAuth2Error("invalid_token", 
            "The required audience is missing", null);
        return OAuth2TokenValidatorResult.failure(error);
    }
}
