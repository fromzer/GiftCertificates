package com.epam.esm.controller;

import com.epam.esm.entity.User;
import com.epam.esm.exception.BadPasswordException;
import com.epam.esm.exception.UserBlockedException;
import com.epam.esm.exception.UserNotFoundException;
import com.epam.esm.model.AuthenticationRequestDTO;
import com.epam.esm.model.RegisteredUser;
import com.epam.esm.model.UserGift;
import com.epam.esm.security.JwtTokenProvider;
import com.epam.esm.service.UserService;
import com.google.common.cache.Cache;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final Cache<String, String> userTokenCache;

    public AuthenticationRestController(AuthenticationManager authenticationManager,
                                        UserService userService,
                                        JwtTokenProvider jwtTokenProvider,
                                        Cache<String, String> userTokenCache) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userTokenCache = userTokenCache;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
            User user = userService.findUserByLogin(request.getLogin());
            String token = jwtTokenProvider.generateToken(user);
            userTokenCache.put(user.getLogin(), token);
            System.out.println(userTokenCache.toString());
            Map<Object, Object> response = new HashMap<>();
            response.put("login", request.getLogin());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (LockedException e) {
            throw new UserBlockedException(e.getMessage());
        } catch (BadCredentialsException ex) {
            userService.changeValueLoginAttemptsAndLockDate(request.getLogin());
            throw new BadPasswordException(ex.getMessage());
        } catch (AuthenticationException exception) {
            throw new UserNotFoundException(exception.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<UserGift> signup(@RequestBody @Valid RegisteredUser registeredUser) {
        return ResponseEntity.ok(userService.createUser(registeredUser));
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtTokenProvider.resolveToken(request);
        String login = jwtTokenProvider.getLogin(token);
        jwtTokenProvider.checkContainsTokenInCache(token, login);
        userTokenCache.invalidate(login);
    }
}
