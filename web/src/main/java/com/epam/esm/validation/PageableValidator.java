package com.epam.esm.validation;

import com.epam.esm.model.Pageable;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

@Component
public class PageableValidator implements Validator {
    private static final String VALIDATE_PAGEABLE_MESSAGE = "validate.pageable";

    private final MessageSource messageSource;

    public PageableValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Pageable.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Locale locale = Locale.getDefault();
        Pageable pageable = (Pageable) target;
        if (pageable.getPage() != null && pageable.getPage() < 1) {
            errors.reject("400", messageSource.getMessage(VALIDATE_PAGEABLE_MESSAGE, null, locale));
        }
        if (pageable.getSize() != null && pageable.getSize() < 1) {
            errors.reject("400", messageSource.getMessage(VALIDATE_PAGEABLE_MESSAGE, null, locale));
        }
        if (pageable.getPage() > 1000 || pageable.getSize() > 1000) {
            errors.reject("400", messageSource.getMessage(VALIDATE_PAGEABLE_MESSAGE, null, locale));
        }
    }
}
