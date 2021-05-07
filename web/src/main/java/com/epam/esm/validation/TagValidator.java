package com.epam.esm.validation;

import com.epam.esm.model.GiftTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Locale;

@Component
public class TagValidator implements Validator {
    private static final String NAME_VALIDATION_PATTERN = ".{1,50}";
    private static final String VALIDATE_NAME_MESSAGE = "validate.name";

    private final MessageSource messageSource;

    @Autowired
    public TagValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return GiftTag.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Locale locale = Locale.getDefault();
        GiftTag giftTag = (GiftTag) target;
        if (!giftTag.getName().matches(NAME_VALIDATION_PATTERN)) {
            errors.reject("400", messageSource.getMessage(VALIDATE_NAME_MESSAGE, null, locale));
        }
    }
}
