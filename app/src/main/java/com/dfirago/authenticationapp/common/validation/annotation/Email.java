package com.dfirago.authenticationapp.common.validation.annotation;

import com.dfirago.authenticationapp.common.validation.rule.EmailRule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dmytro Firago on 06/06/2017.
 */
@Validation(EmailRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Email {

    boolean allowLocal() default false;

    int messageResId() default -1;

    String message() default "Invalid email";
}
