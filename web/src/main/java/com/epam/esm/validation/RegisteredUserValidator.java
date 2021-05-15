//package com.epam.esm.validation;
//
//import com.epam.esm.model.RegisteredUser;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.MessageSource;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.Errors;
//import org.springframework.validation.Validator;
//
//import java.util.Locale;
//
//@Component
//public class RegisteredUserValidator implements Validator {
//    private static final String EMPTY_FIELDS_MESSAGE = "validate.emptyFields";
//
//    private final MessageSource messageSource;
//
//    @Autowired
//    public RegisteredUserValidator(MessageSource messageSource) {
//        this.messageSource = messageSource;
//    }
//
//    @Override
//    public boolean supports(Class<?> clazz) {
//        return RegisteredUser.class.equals(clazz);
//    }
//
//    @Override
//    public void validate(Object target, Errors errors) {
//        Locale locale = Locale.getDefault();
//        RegisteredUser user = (RegisteredUser) target;
//        if (user.getLogin() == null) {
//            errors.reject("400", messageSource.getMessage(EMPTY_FIELDS_MESSAGE, null, locale));
//        }
//        if (user.getFirstName() == null) {
//            errors.reject("400", messageSource.getMessage(EMPTY_FIELDS_MESSAGE, null, locale));
//        }
//        if (user.getLastName() == null) {
//            errors.reject("400", messageSource.getMessage(EMPTY_FIELDS_MESSAGE, null, locale));
//        }
//        if (user.getPassword() == null) {
//            errors.reject("400", messageSource.getMessage(EMPTY_FIELDS_MESSAGE, null, locale));
//        }
//    }
//}
