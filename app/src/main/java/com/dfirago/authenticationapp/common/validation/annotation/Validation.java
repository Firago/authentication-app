package com.dfirago.authenticationapp.common.validation.annotation;

import com.dfirago.authenticationapp.common.validation.rule.ValidationRule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dmytro Firago on 04/06/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Validation {

    Class<? extends ValidationRule> value();
}
