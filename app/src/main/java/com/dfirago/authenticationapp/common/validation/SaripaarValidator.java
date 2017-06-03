package com.dfirago.authenticationapp.common.validation;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Validator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Dmytro Firago on 03/06/2017.
 */
public final class SaripaarValidator {

    private SaripaarValidator() {

    }

    @UiThread
    public static void bind(@NonNull Activity target) {
        bind(target, new DefaultValidationListener(target.getApplicationContext()));
    }

    @UiThread
    public static void bind(@NonNull Activity target,
                            @NonNull Validator.ValidationListener fieldValidationListener) {
        // initialize validator
        final Validator validator = new Validator(target);
        validator.setValidationListener(fieldValidationListener);
        // bind field validations
        bindValidations(target, validator);
        // bind validation triggers
        bindTriggers(target, validator);
    }

    private static void bindValidations(@NonNull Activity target, Validator validator) {
        final Class<?> targetClass = target.getClass();
        final Field[] fields = targetClass.getFields();
        for (Field field : fields) {
            // TextView fields may be annotated with validations
            if (TextView.class.isAssignableFrom(field.getType())) {
                try {
                    final TextView view = (TextView) field.get(target);
                    // add validation on fields marked with OnFocusLostValidation
                    if (field.isAnnotationPresent(OnFocusLostValidation.class)) {
                        // if OnFocusChangeListener is already added, it will be extended
                        addOnFocusChangeListener(view, (v, hasFocus) -> {
                            if (!hasFocus) {
                                validator.validateTill(v);
                            }
                        });
                    }
                    // add validation on fields marked with OnTextChangedValidation
                    if (field.isAnnotationPresent(OnTextChangedValidation.class)) {
                        addOnTextChangedListener(view, validator::validateTill);
                    }

                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unable to retrieve field value: " + field, e);
                }
            }
        }
    }

    private static void bindTriggers(Activity target, Validator validator) {
        final Class<?> targetClass = target.getClass();
        final Field[] fields = targetClass.getFields();
        for (Field field : fields) {
            // View fields may be annotated with triggers
            if (View.class.isAssignableFrom(field.getType())) {
                try {
                    final View view = (View) field.get(target);
                    // add validation on fields marked with OnFocusLostValidation
                    if (field.isAnnotationPresent(ValidationTrigger.class)) {
                        addOnClickListener(view, v -> validator.validate());
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unable to retrieve field value: " + field, e);
                }
            }
        }
    }

    private static View.OnClickListener getExistingOnClickListener(View view) {
        final Class<?> viewClass = View.class; // view.getClass() can't find private fields from base class
        try {
            final Field listenerInfoField = viewClass.getDeclaredField("mListenerInfo");
            if (listenerInfoField != null) {
                listenerInfoField.setAccessible(true);
                final Object listenerInfo = listenerInfoField.get(view);
                final Class<?> listenerInfoClass = Class.forName("android.view.View$ListenerInfo");
                final Field listenerField = listenerInfoClass.getDeclaredField("mOnClickListener");
                if (listenerField != null && listenerInfo != null) {
                    return (View.OnClickListener) listenerField.get(listenerInfo);
                }
            }
            return null;
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException("Unable to retrieve existing listener", e);
        }
    }

    private static void addOnClickListener(View view, View.OnClickListener onClickListener) {
        final View.OnClickListener existingListener = getExistingOnClickListener(view);
        if (existingListener != null) {
            final Method existingListenerMethod = getExistingOnClickListenerMethod(existingListener);
            view.setOnClickListener(v -> {
                onClickListener.onClick(v);
                try {
                    existingListenerMethod.invoke(existingListener, v);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to execute existing listener method", e);
                }
            });
        }
    }

    private static Method getExistingOnClickListenerMethod(View.OnClickListener existingListener) {
        final Class<?> listenerClass = existingListener.getClass();
        try {
            return listenerClass.getMethod("onClick", View.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to retrieve listener method", e);
        }
    }

    private static void addOnFocusChangeListener(TextView view, View.OnFocusChangeListener listener) {
        final Method existingMethod = getExistingOnFocusChangeListenerMethod(view);
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

    private static void addOnTextChangedListener(TextView textView, OnTextChangedListener listener) {
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

    private static Method getExistingOnFocusChangeListenerMethod(View view) {
        final View.OnFocusChangeListener existingListener = view.getOnFocusChangeListener();
        if (existingListener != null) {
            Class<?> listenerClass = existingListener.getClass();
            try {
                return listenerClass.getDeclaredMethod("onFocusChange", listenerClass);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unable to retrieve listener method", e);
            }
        }
        return null;
    }

    private interface OnTextChangedListener {
        void onTextChanged(TextView textView);
    }
}
