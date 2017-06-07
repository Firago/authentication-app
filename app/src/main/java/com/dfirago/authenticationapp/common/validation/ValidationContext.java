package com.dfirago.authenticationapp.common.validation;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.ArrayMap;
import android.view.View;

import com.dfirago.authenticationapp.common.validation.annotation.Validation;
import com.dfirago.authenticationapp.common.validation.rule.ValidationRule;
import com.dfirago.authenticationapp.common.validation.utils.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Dmytro Firago on 06/06/2017.
 */

public class ValidationContext {

    private final Object controller;
    private final Context context;
    private final ArrayMap<View, Set<ValidationRule>> ruleMap;

    ValidationContext(final Object controller) {
        this.controller = controller;
        this.context = getContext(controller);
        this.ruleMap = buildRuleMap(controller);
    }

    private Context getContext(Object controller) {
        if (controller instanceof Activity) {
            Activity activity = (Activity) controller;
            return activity.getApplicationContext();
        } else if (controller instanceof Fragment) {
            Activity activity = ((Fragment) controller).getActivity();
            return activity.getApplicationContext();
        } else {
            throw new IllegalArgumentException(
                    "ValidationContext can not be used with " + controller.getClass());
        }
    }

    private ArrayMap<View, Set<ValidationRule>> buildRuleMap(final Object controller) {

        final ArrayMap<View, Set<ValidationRule>> result = new ArrayMap<>();

        final List<Field> annotatedFields = ReflectionUtils
                .getAnnotatedControllerFields(controller.getClass(), Validation.class);

        for (Field annotatedField : annotatedFields) {
            final Set<ValidationRule> rules = new HashSet<>();
            Set<Annotation> annotations = ReflectionUtils.
                    getAnnotationsRecursively(annotatedField, Validation.class);
            for (Annotation annotation : annotations) {
                Class<? extends ValidationRule> ruleType
                        = ReflectionUtils.getValidationRuleType(annotation);
                final ValidationRule validationRule = ReflectionUtils
                        .instantiateRule(ruleType, annotation, context);
                rules.add(validationRule);
            }
            final View view = (View) ReflectionUtils
                    .getFieldValue(annotatedField, controller);

            result.put(view, rules);
        }

        return result;
    }

    public Object getController() {
        return controller;
    }

    public Set<View> getViews() {
        return ruleMap.keySet();
    }

    public Set<ValidationRule> getRules(View view) {
        return ruleMap.get(view);
    }
}
