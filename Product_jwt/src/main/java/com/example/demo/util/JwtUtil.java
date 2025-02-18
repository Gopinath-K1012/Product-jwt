package com.example.demo.util;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final int jwtExpirationMs = 6000000; // 1 hour
    private final UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.secretKey = generateSecretKey();
        this.userRepository = userRepository;
    }

    // Generate a new secret key
    private SecretKey generateSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    // Generate token
    public String generateToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    // Extract user_name
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Token validation
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

	public boolean validateToken(String token, UserDetails userDetails) {
		// TODO Auto-generated method stub
		return false;
	}
}
