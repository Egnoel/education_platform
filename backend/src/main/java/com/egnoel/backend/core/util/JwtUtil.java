package com.egnoel.backend.core.util;

import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    // Chave secreta gerada para assinatura do token (HS256)
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // Tempo de expiração do token: 24 horas (em milissegundos)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * Gera um token JWT com base no e-mail e no papel do utilizador.
     * @param email O e-mail do utilizador (subject do token)
     * @param role O papel do utilizador ("PROFESSOR" ou "ALUNO")
     * @return O token JWT gerado
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Adiciona o papel como claim
        return createToken(claims, email);
    }

    /**
     * Cria o token JWT com os claims e o subject fornecidos.
     * @param claims Mapa de claims adicionais (ex.: role)
     * @param subject O subject do token (e-mail do utilizador)
     * @return O token JWT como string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai o e-mail (subject) do token.
     * @param token O token JWT
     * @return O e-mail do utilizador
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai o papel (role) do token.
     * @param token O token JWT
     * @return O papel do utilizador ("PROFESSOR" ou "ALUNO")
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extrai um claim específico do token.
     * @param token O token JWT
     * @param claimsResolver Função que extrai o claim desejado
     * @return O valor do claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai todos os claims do token.
     * @param token O token JWT
     * @return Os claims解析ados
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Token inválido ou expirado", e);
        }
    }

    /**
     * Valida o token comparando o e-mail e verificando a expiração.
     * @param token O token JWT
     * @param email O e-mail do utilizador a validar
     * @return true se o token é válido, false caso contrário
     */
    public boolean validateToken(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    /**
     * Verifica se o token está expirado.
     * @param token O token JWT
     * @return true se expirado, false caso contrário
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai a data de expiração do token.
     * @param token O token JWT
     * @return A data de expiração
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
