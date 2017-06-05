package com.dfirago.authenticationapp.common.validation;

import android.view.View;

import com.dfirago.authenticationapp.common.validation.rule.ValidationRule;

import java.util.Set;

/**
 * Created by Dmytro Firago on 05/06/2017.
 */

public class ValidationResult {

    private final View view;
    private final Set<ValidationRule> failedRules;

    public ValidationResult(final View view, final Set<ValidationRule> failedRules) {
        this.view = view;
        this.failedRules = failedRules;
    }

    public View getView() {
        return view;
    }

    public Set<ValidationRule> getFailedRules() {
        return failedRules;
    }

    public String getCollatedErrorMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        if (failedRules != null && !failedRules.isEmpty()) {
            for (ValidationRule failedRule : failedRules) {
                String message = failedRule.getErrorMessage().trim();
                if (message.length() > 0) {
                    stringBuilder.append(message).append("\n");
                }
            }
        }
        return stringBuilder.toString().trim();
    }

    public boolean isFailed() {
        return failedRules != null && !failedRules.isEmpty();
    }
}
