package com.dfirago.authenticationapp.common.validation;

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
    public static Validator bind(@NonNull final Object controller) {
        return bind(controller, new Validator.DefaultValidationListener());
    }

    @UiThread
    public static Validator bind(@NonNull final Object controller,
                                 @NonNull final Validator.ValidationListener validationListener) {
        final ValidationContext validationContext = new ValidationContext(controller);
        final Validator validator = new Validator(validationContext);
        validator.setValidationListener(validationListener);
        // bind validation listeners
        bindListeners(controller, validator);
        // bind validation triggers
        bindTriggers(controller, validator);

        return validator;
    }

    private static void bindListeners(@NonNull final Object controller,
                                      @NonNull final Validator validator) {
        final List<Field> fields = ReflectionUtils
                .getControllerFields(controller.getClass(), TextView.class);
        for (Field field : fields) {
            final TextView view = (TextView) ReflectionUtils.getFieldValue(field, controller);
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

    private static void bindTriggers(@NonNull final Object controller,
                                     @NonNull final Validator validator) {
        final List<Field> fields = ReflectionUtils
                .getControllerFields(controller.getClass(), View.class);
        for (Field field : fields) {
            final View view = (View) ReflectionUtils.getFieldValue(field, controller);
            // add validation on fields marked with ValidationTrigger
            if (field.isAnnotationPresent(ValidationTrigger.class)) {
                final View.OnClickListener existingListener
                        = ReflectionUtils.getOnClickListener(view);
                final Method existingListenerMethod =
                        existingListener == null ? null
                                : ReflectionUtils.getOnClickListenerMethod(existingListener);
                view.setOnClickListener(v -> {
                    BatchValidationResult validationResult = validator.validate();
                    if (existingListenerMethod != null && !validationResult.isFailed()) {
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
