package com.dfirago.authenticationapp.common.validation.annotation;

import com.dfirago.authenticationapp.common.validation.rule.NotEmptyRule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dmytro Firago on 04/06/2017.
 */
@Validation(NotEmptyRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface NotEmpty {

    int messageResId() default -1;

    String message() default "This field is required";
}
