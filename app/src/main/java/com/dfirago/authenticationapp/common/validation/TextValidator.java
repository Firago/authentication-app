package com.dfirago.authenticationapp.common.validation;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by Dmytro Firago on 02/06/2017.
 */

public abstract class TextValidator implements TextWatcher {

    private final TextView textView;

    public TextValidator(TextView textView) {
        this.textView = textView;
    }

    public abstract void validate(TextView textView);

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Not needed - skip
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Not needed - skip
    }

    @Override
    public void afterTextChanged(Editable editable) {
        validate(textView);
    }
}
