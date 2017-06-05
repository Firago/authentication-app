package com.dfirago.authenticationapp.common.validation;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.dfirago.authenticationapp.common.validation.annotation.ValidationListener;
import com.dfirago.authenticationapp.common.validation.annotation.ValidationTrigger;
import com.dfirago.authenticationapp.common.validation.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Dmytro Firago on 03/06/2017.
 */
public abstract class ValidationBinder {

    @UiThread
    public static void bind(@NonNull final Activity target) {
        bind(target, new Validator.DefaultValidationListener());
    }

    @UiThread
    public static void bind(@NonNull final Activity target,
                            @NonNull final Validator.ValidationListener validationListener) {
        // initialize validator
        final Validator validator = new Validator(target);
        validator.setValidationListener(validationListener);
        // bind validation listeners
        bindListeners(target, validator);
        // bind validation triggers
        bindTriggers(target, validator);
    }

    private static void bindListeners(@NonNull final Activity target,
                                      @NonNull final Validator validator) {
        final List<Field> fields = ReflectionUtils
                .getControllerFields(target.getClass(), TextView.class);
        for (Field field : fields) {
            final TextView view = (TextView) ReflectionUtils.getFieldValue(field, target);
            final ValidationListener validationListener
                    = field.getAnnotation(ValidationListener.class);
            if (validationListener != null) {
                if (validationListener.onFocusLost()) {
                    addOnFocusChangeListener(view, (v, hasFocus) -> {
                        if (!hasFocus) {
                            validator.validate(v);
                        }
                    });
                }
                if (validationListener.onTextChanged()) {
                    addOnTextChangedListener(view, validator::validate);
                }
            }
        }
    }

    private static void bindTriggers(@NonNull final Activity target,
                                     @NonNull final Validator validator) {
        final List<Field> fields = ReflectionUtils
                .getControllerFields(target.getClass(), View.class);
        for (Field field : fields) {
            final View view = (View) ReflectionUtils.getFieldValue(field, target);
            // add validation on fields marked with ValidationTrigger
            if (field.isAnnotationPresent(ValidationTrigger.class)) {
                final View.OnClickListener existingListener
                        = ReflectionUtils.getOnClickListener(view);
                if (existingListener != null) {
                    final Method existingListenerMethod = ReflectionUtils
                            .getOnClickListenerMethod(existingListener);
                    view.setOnClickListener(v -> {
                        BatchValidationResult validationResult = validator.validate();
                        if (!validationResult.isFailed()) {
                            try {
                                existingListenerMethod.invoke(existingListener, v);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException("Failed to execute existing listener method", e);
                            }
                        }
                    });
                }
            }
        }
    }

    private static void addOnFocusChangeListener(@NonNull final TextView view,
                                                 @NonNull final View.OnFocusChangeListener listener) {
        final Method existingMethod = ReflectionUtils.getOnFocusChangeListenerMethod(view);
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (existingMethod != null) {
                try {
                    existingMethod.invoke(view.getOnFocusChangeListener(), v, hasFocus);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to execute existing listener method", e);
                }
            }
            listener.onFocusChange(v, hasFocus);
        });
    }

    private static void addOnTextChangedListener(@NonNull final TextView textView,
                                                 @NonNull final OnTextChangedListener listener) {
        textView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                listener.onTextChanged(textView);
            }
        });
    }

    private interface OnTextChangedListener {
        void onTextChanged(@NonNull final TextView textView);
    }
}
