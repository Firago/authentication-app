package com.dfirago.authenticationapp.common.validation;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import java.util.List;

/**
 * Created by Dmytro Firago on 02/06/2017.
 */
public class DefaultValidationListener implements Validator.ValidationListener {

    private final Context context;

    public DefaultValidationListener(Context context) {
        this.context = context;
    }

    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(context);
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
        }
    }
}
