package com.epam.esm.security;

import com.epam.esm.entity.Role;
import com.epam.esm.entity.User;
import com.epam.esm.exception.JwtAuthenticationException;
import com.epam.esm.service.UserService;
import com.google.common.cache.Cache;
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
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final static String TOKEN_SUB_STRING = "Bearer ";
    private final static String EMPTY = "";
    private final UserService userService;

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.header}")
    private String authorizationHeader;
    @Value("${jwt.expirationInMinutes}")
    private long expirationInMinutes;
    @Value("${date.time-zone}")
    private String timeZone;
    private final Cache<String, String> userTokenCache;


    @Autowired
    public JwtTokenProvider(UserService userService, Cache<String, String> userTokenCache) {
        this.userService = userService;
        this.userTokenCache = userTokenCache;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String generateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getLogin());
        claims.put("role", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        ZonedDateTime now = LocalDateTime.now().atZone(ZoneId.of(timeZone));
        ZonedDateTime validity = LocalDateTime.now().plusMinutes(expirationInMinutes).atZone(ZoneId.of(timeZone));
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
            String login = claimsJws.getBody().getSubject();
            checkContainsTokenInCache(token, login);
            return !claimsJws.getBody().getExpiration().before(Date.from(LocalDateTime.now().atZone(ZoneId.of(timeZone)).toInstant()));
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException("Token is expired or invalid");
        }
    }

    public void checkContainsTokenInCache(String token, String login) {
        String cacheIfPresentToken = userTokenCache.getIfPresent(login);
        if (cacheIfPresentToken == null || !cacheIfPresentToken.equals(token)) {
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
