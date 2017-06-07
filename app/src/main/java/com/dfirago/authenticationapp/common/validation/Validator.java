package com.dfirago.authenticationapp.common.validation;

import android.view.View;
import android.widget.TextView;

import com.dfirago.authenticationapp.common.validation.rule.ValidationRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Dmytro Firago on 04/06/2017.
 */
public class Validator {

    private final ValidationContext validationContext;

    private ValidationListener validationListener;

    public Validator(final ValidationContext validationContext) {
        this.validationContext = validationContext;
        this.validationListener = new DefaultValidationListener();
    }

    public void setValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @SuppressWarnings("unchecked")
    public ValidationResult validate(View view) {
        final Set<ValidationRule> failedRules = new HashSet<>();
        final Set<ValidationRule> ruleSet = validationContext.getRules(view);
        ruleSet.stream()
                .filter(rule -> TextView.class.isAssignableFrom(view.getClass())) // TODO data adapters
                .forEach(rule -> {
                    String data = ((TextView) view).getText().toString();
                    if (!rule.validate(validationContext, data)) {
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
        validationContext.getViews()
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