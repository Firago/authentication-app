package com.dfirago.authenticationapp.common.validation.rule;

import android.content.Context;

import com.dfirago.authenticationapp.common.validation.ValidationContext;
import com.dfirago.authenticationapp.common.validation.annotation.NotEmpty;

/**
 * Created by Dmytro Firago on 04/06/2017.
 */
public class NotEmptyRule extends ValidationRule<NotEmpty, String> {

    protected NotEmptyRule(final Context context, final NotEmpty annotation) {
        super(context, annotation);
    }

    @Override
    public boolean validate(final ValidationContext validationContext, final String s) {
        return s != null && !s.isEmpty();
    }
}
