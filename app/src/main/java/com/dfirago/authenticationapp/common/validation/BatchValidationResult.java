package com.dfirago.authenticationapp.common.validation;

import java.util.List;

/**
 * Created by Dmytro Firago on 05/06/2017.
 */

public class BatchValidationResult {

    private final List<ValidationResult> validationResults;

    public BatchValidationResult(List<ValidationResult> validationResults) {
        this.validationResults = validationResults;
    }

    public boolean isFailed() {
        for (ValidationResult validationResult : validationResults) {
            if (validationResult.isFailed()) {
                return true;
            }
        }
        return false;
    }
}
