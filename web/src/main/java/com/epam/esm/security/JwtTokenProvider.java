package com.epam.esm.security;

import com.epam.esm.entity.User;
import com.epam.esm.exception.JwtAuthenticationException;
import com.epam.esm.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtTokenProvider {
    private final static String TOKEN_SUB_STRING = "Bearer ";
    private final static String EMPTY = "";
    private final static String ZONE = "+03:00";
    private final UserService userService;

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.header}")
    private String authorizationHeader;
    @Value("${jwt.expirationInMinutes}")
    private long expirationInMinutes;

    @Autowired
    public JwtTokenProvider(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String generateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getLogin());
        claims.put("role", user.getRoles());
        ZonedDateTime now = LocalDateTime.now().atZone(ZoneId.of(ZONE));
        ZonedDateTime validity = LocalDateTime.now().plusMinutes(expirationInMinutes).atZone(ZoneId.of(ZONE));
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(validity.toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            Optional.ofNullable(claimsJws.getBody().getExpiration())
                    .orElseThrow(() -> new JwtAuthenticationException("Token is expired or invalid"));
            return !claimsJws.getBody().getExpiration().before(Date.from(LocalDateTime.now().atZone(ZoneId.of(ZONE)).toInstant()));
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("Token is expired or invalid");
        }
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userService.loadUserByUsername(getLogin(token));
        return new UsernamePasswordAuthenticationToken(userDetails, EMPTY, userDetails.getAuthorities());
    }

    public String getLogin(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(authorizationHeader);
        return Optional.ofNullable(header)
                .map(this::getString)
                .orElse(null);
    }

    private String getString(String header) {
        return header.replace(TOKEN_SUB_STRING, EMPTY).trim();
    }
}
