package com.epam.esm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisteredUser {
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{1,30}", message = "validate.login")
    private String login;

    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{1,30}", message = "validate.firstName")
    private String firstName;

    @Pattern(regexp = "[A-Za-zА-Яа-яЁё \\-]{1,50}", message = "validate.lastName")
    private String lastName;

    @Pattern(regexp = "[A-Za-zА-Яа-яЁё0-9!?@#$%^&*()\\-_+:;,.]{3,255}", message = "validate.password")
    private String password;

    @Pattern(regexp = "[A-Za-zА-Яа-яЁё0-9!?@#$%^&*()\\-_+:;,.]{3,255}", message = "validate.password")
    private String repeatPassword;
}
