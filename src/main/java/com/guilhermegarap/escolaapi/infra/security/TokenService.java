package com.guilhermegarap.escolaapi.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.guilhermegarap.escolaapi.domain.usuario.Usuario;

@Service
public class TokenService {
    
    @Value("${api.security.jwt.secret}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        System.out.println(secret);
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                .withIssuer("API Escola")
                .withSubject(usuario.getEmail())
                .withClaim("role", usuario.getRole())
                .withExpiresAt(dataExpiracao())
                .sign(algoritmo);
            } catch (JWTCreationException exception){
                throw new RuntimeException("Erro ao criar token JWT: " + exception.getMessage());
            }
                }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubject(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                // specify any specific claim validations
                .withIssuer("API Escola")
                .build()
                .verify(tokenJWT).getSubject();
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Token JWT inválido ou expirado! " + exception.getMessage());
        }
}
}
