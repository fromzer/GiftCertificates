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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationRestController(AuthenticationManager authenticationManager,
                                        UserService userService,
                                        JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
            User user = userService.findUserByLogin(request.getLogin());
            String token = jwtTokenProvider.generateToken(user);
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
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<UserGift> signup(@RequestBody @Valid RegisteredUser registeredUser) {
        return ResponseEntity.ok(userService.createUser(registeredUser));
    }
}
