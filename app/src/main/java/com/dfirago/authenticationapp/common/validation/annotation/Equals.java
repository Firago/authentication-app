package com.dfirago.authenticationapp.common.validation.annotation;

import com.dfirago.authenticationapp.common.validation.rule.EqualsRule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dmytro Firago on 07/06/2017.
 */
@Validation(EqualsRule.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface Equals {

    String property();

    int messageResId() default -1;

    String message() default "Values do not match";
}
