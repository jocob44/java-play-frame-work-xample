package security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.typesafe.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.Date;

@Singleton
public class JwtService {
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final String issuer;
    private final long expirationSeconds;

    @Inject
    public JwtService(Config config) {
        String secret = config.getString("jwt.secret");
        this.issuer = config.getString("jwt.issuer");
        this.expirationSeconds = config.getLong("jwt.expirationSeconds");
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer(issuer).build();
    }

    public String createToken(long userId, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirationSeconds);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(userId))
                .withClaim("email", email)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm);
    }

    public long parseUserId(String token) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            return Long.parseLong(decodedJWT.getSubject());
        } catch (JWTVerificationException | NumberFormatException ex) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }
    }
}
