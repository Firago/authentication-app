package com.dfirago.authenticationapp.common.validation;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;

import com.mobsandgeeks.saripaar.Validator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Dmytro Firago on 02/06/2017.
 */
public final class OnFocusLostValidations {

    private OnFocusLostValidations() {

    }

    @UiThread
    public static void bind(@NonNull Activity target) {
        Validator validator = new Validator(target);
        validator.setValidationListener(new DefaultValidationListener(target.getApplicationContext()));
        Class<?> clazz = target.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(OnFocusLostValidation.class)
                    && View.class.isAssignableFrom(field.getType())) {
                try {
                    View view = (View) field.get(target);
                    Method existingListenerMethod = getExistingListenerMethod(view);
                    view.setOnFocusChangeListener((v, hasFocus) -> {
                        if (existingListenerMethod != null) {
                            try {
                                existingListenerMethod.invoke(view.getOnFocusChangeListener(), v, hasFocus);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException("Failed to execute existing listener method", e);
                            }
                        }
                        if (!hasFocus) {
                            validator.validateTill(v);
                        }
                    });
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Unable to retrieve field value: " + field, e);
                }
            }
        }
    }

    private static Method getExistingListenerMethod(View view) {
        View.OnFocusChangeListener externalListener = view.getOnFocusChangeListener();
        if (externalListener != null) {
            Class<View.OnFocusChangeListener> listenerClass = View.OnFocusChangeListener.class;
            try {
                return listenerClass.getDeclaredMethod("onFocusChange", listenerClass);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unable to retrieve listener method", e);
            }
        }
        return null;
    }
}
