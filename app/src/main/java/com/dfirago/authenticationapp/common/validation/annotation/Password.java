package com.dfirago.authenticationapp.common.validation.annotation;

import com.dfirago.authenticationapp.common.validation.rule.PasswordRule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dmytro Firago on 06/06/2017.
 */
@Validation(PasswordRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Password {

    int min() default 6;

    Scheme scheme() default Scheme.ANY;

    int messageResId() default -1;

    String message() default "Password doesn't meet requirements";

    enum Scheme {
        ANY,
        ALPHA,
        ALPHA_MIXED_CASE,
        NUMERIC, ALPHA_NUMERIC,
        ALPHA_NUMERIC_MIXED_CASE,
        ALPHA_NUMERIC_SYMBOLS,
        ALPHA_NUMERIC_MIXED_CASE_SYMBOLS
    }
}
