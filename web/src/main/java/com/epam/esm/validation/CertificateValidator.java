package com.epam.esm.validation;

import com.epam.esm.model.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.Locale;

@Component
public class CertificateValidator implements Validator {
    private static final String NAME_VALIDATION_PATTERN = ".{1,50}";
    private static final String DESCRIPTION_VALIDATION_PATTERN = ".{1,500}";
    private static final String VALIDATE_NAME_MESSAGE = "validate.name";
    private static final String VALIDATE_DESCRIPTION_MESSAGE = "validate.description";
    private static final String VALIDATE_PRICE_MESSAGE = "validate.price";
    private static final String VALIDATE_DURATION_MESSAGE = "validate.duration";

    private final MessageSource messageSource;

    @Autowired
    public CertificateValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return GiftCertificate.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Locale locale = Locale.getDefault();
        GiftCertificate giftCertificate = (GiftCertificate) target;
        if (giftCertificate.getName() != null && !giftCertificate.getName().matches(NAME_VALIDATION_PATTERN)) {
            errors.reject("400", messageSource.getMessage(VALIDATE_NAME_MESSAGE, null, locale));
        }
        if (giftCertificate.getDescription() != null && !giftCertificate.getDescription().matches(DESCRIPTION_VALIDATION_PATTERN)) {
            errors.reject("400", messageSource.getMessage(VALIDATE_DESCRIPTION_MESSAGE, null, locale));
        }
        if (giftCertificate.getPrice() != null && giftCertificate.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.reject("400", messageSource.getMessage(VALIDATE_PRICE_MESSAGE, null, locale));
        }
        if (giftCertificate.getDuration() != null && giftCertificate.getDuration() < 0) {
            errors.reject("400", messageSource.getMessage(VALIDATE_DURATION_MESSAGE, null, locale));
        }
    }
}
