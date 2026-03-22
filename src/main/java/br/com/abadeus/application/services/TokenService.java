package br.com.abadeus.application.services;

import br.com.abadeus.domain.interfaces.AuthUser;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${spring.secretkey}")
    private String secret;

    @Value("${spring.tempo_expiracao}")
    private Long tempo;

    private final String EMISSOR = "ABADEUS";

    public String gerarToken(AuthUser user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(EMISSOR)
                .withSubject(user.getEmail())
                .withClaim("role", user.getRole())
                .withClaim("userId", user.getId())
                .withExpiresAt(this.gerarDataExpiracao())
                .sign(algorithm);
    }

    public String extrairEmailDoToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(EMISSOR)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token inválido ou expirado.");
        }
    }

    private Instant gerarDataExpiracao() {
        return LocalDateTime.now().plusMinutes(tempo).toInstant(ZoneOffset.of("-03:00"));
    }
}