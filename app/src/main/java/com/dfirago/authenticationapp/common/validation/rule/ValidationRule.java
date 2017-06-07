package com.dfirago.authenticationapp.common.validation.rule;

import android.content.Context;

import com.dfirago.authenticationapp.common.validation.ValidationContext;
import com.dfirago.authenticationapp.common.validation.utils.ReflectionUtils;

import java.lang.annotation.Annotation;

/**
 * Created by Dmytro Firago on 04/06/2017.
 */
public abstract class ValidationRule<AnnotationType extends Annotation, Validatable> {

    protected final Context context;
    protected final AnnotationType annotation;

    protected ValidationRule(final Context context, final AnnotationType annotation) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (annotation == null) {
            throw new IllegalArgumentException("Annotation cannot be null");
        }
        this.context = context;
        this.annotation = annotation;
    }

    public abstract boolean validate(final ValidationContext validationContext,
                                     final Validatable validatable);

    public String getErrorMessage() {
        final int messageResId = ReflectionUtils
                .getAnnotationAttributeValue(annotation, "messageResId", Integer.class);

        return messageResId != -1
                ? context.getString(messageResId)
                : ReflectionUtils.getAnnotationAttributeValue(annotation, "message", String.class);
    }
}
