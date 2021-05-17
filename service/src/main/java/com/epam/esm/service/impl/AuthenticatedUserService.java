package com.epam.esm.service.impl;

import com.epam.esm.entity.User;
import com.epam.esm.exception.UserNotFoundException;
import com.epam.esm.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {
    private final UserRepository userRepository;

    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean hasId(Long id) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        User byLogin = userRepository.findByLogin(login)
                .orElseThrow(UserNotFoundException::new);
        return byLogin.getId().equals(id);
    }
}
