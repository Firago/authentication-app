package com.dfirago.authenticationapp.common.validation.rule;

import android.content.Context;

import com.dfirago.authenticationapp.common.validation.ValidationContext;
import com.dfirago.authenticationapp.common.validation.annotation.Email;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * Created by Dmytro Firago on 06/06/2017.
 */
public class EmailRule extends ValidationRule<Email, String> {

    protected EmailRule(Context context, Email annotation) {
        super(context, annotation);
    }

    @Override
    public boolean validate(final ValidationContext validationContext, final String email) {
        EmailValidator emailValidator = EmailValidator.getInstance(annotation.allowLocal());
        return emailValidator.isValid(email);
    }
}
