package com.dfirago.authenticationapp.common.validation;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Validator;

import java.lang.reflect.Field;

/**
 * Created by Dmytro Firago on 02/06/2017.
 */

public final class OnTextChangedValidations {

    private OnTextChangedValidations() {

    }

    @UiThread
    public static void bind(@NonNull Activity target) {
        Validator validator = new Validator(target);
        validator.setValidationListener(new DefaultValidationListener(target.getApplicationContext()));
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(OnTextChangedValidation.class)
                    && TextView.class.isAssignableFrom(field.getType())) {
                try {
                    TextView textView = (TextView) field.get(target);
                    textView.addTextChangedListener(new TextValidator(textView) {
                        @Override
                        public void validate(TextView view) {
                            validator.validateTill(view);
                        }
                    });
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unable to retrieve field value: " + field, e);
                }
            }
        }
    }
}
