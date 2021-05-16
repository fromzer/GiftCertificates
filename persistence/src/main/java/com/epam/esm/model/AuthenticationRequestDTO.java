package com.epam.esm.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class AuthenticationRequestDTO {
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{1,30}", message = "validate.login")
    private String login;
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё0-9!?@#$%^&*()\\-_+:;,.]{3,255}", message = "validate.password")
    private String password;
}
