package com.dfirago.authenticationapp.common.validation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.ArrayMap;
import android.view.View;
import android.widget.TextView;

import com.dfirago.authenticationapp.common.validation.annotation.Validation;
import com.dfirago.authenticationapp.common.validation.rule.ValidationRule;
import com.dfirago.authenticationapp.common.validation.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Dmytro Firago on 04/06/2017.
 */
public class Validator {

    private final ArrayMap<View, Set<ValidationRule>> ruleMap = new ArrayMap<>();

    private final Context context;

    private ValidationListener validationListener;

    public Validator(final Object controller) {
        if (controller instanceof Activity) {
            Activity activity = (Activity) controller;
            context = activity.getApplicationContext();
        } else if (controller instanceof Fragment) {
            Activity activity = ((Fragment) controller).getActivity();
            context = activity.getApplicationContext();
        } else {
            throw new IllegalArgumentException(
                    "Validator can not be used with " + controller.getClass());
        }

        validationListener = new DefaultValidationListener();

        final List<Field> annotatedFields = ReflectionUtils
                .getAnnotatedControllerFields(controller.getClass(), Validation.class);

        for (Field annotatedField : annotatedFields) {
            final Set<ValidationRule> rules = new HashSet<>();
            Set<Annotation> annotations = ReflectionUtils.
                    getAnnotationsRecursively(annotatedField, Validation.class);
            for (Annotation annotation : annotations) {
                Class<? extends ValidationRule> ruleType
                        = ReflectionUtils.getValidationRuleType(annotation);
                final ValidationRule validationRule = ReflectionUtils
                        .instantiateRule(ruleType, annotation, context);
                rules.add(validationRule);
            }
            final View view = (View) ReflectionUtils
                    .getFieldValue(annotatedField, controller);
            ruleMap.put(view, rules);
        }
    }

    public ValidationListener getValidationListener() {
        return validationListener;
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @SuppressWarnings("unchecked")
    public ValidationResult validate(View view) {
        final Set<ValidationRule> failedRules = new HashSet<>();
        final Set<ValidationRule> ruleSet = ruleMap.get(view);
        ruleSet.stream()
                .filter(rule -> TextView.class.isAssignableFrom(view.getClass())) // TODO data adapters
                .forEach(rule -> {
                    String data = ((TextView) view).getText().toString();
                    if (!rule.validate(data)) {
                        failedRules.add(rule);
                    }
                });
        ValidationResult validationResult = new ValidationResult(view, failedRules);
        if (validationListener != null) {
            validationListener.onValidationFinished(validationResult);
        }
        return validationResult;
    }

    public BatchValidationResult validate() {
        final List<ValidationResult> validationResults = new ArrayList<>();
        ruleMap.keySet()
                .stream()
                .map(this::validate)
                .forEach(validationResults::add);
        return new BatchValidationResult(validationResults);
    }

    public interface ValidationListener {
        void onValidationFinished(ValidationResult validationResult);
    }

    public static class DefaultValidationListener implements ValidationListener {

        @Override
        public void onValidationFinished(ValidationResult validationResult) {
            View view = validationResult.getView();
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                if (validationResult.isFailed()) {
                    textView.setError(validationResult.getCollatedErrorMessage());
                } else {
                    textView.setError(null);
                }
            }
        }
    }
}