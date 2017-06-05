package com.dfirago.authenticationapp.common.validation.utils;

import android.content.Context;
import android.view.View;

import com.dfirago.authenticationapp.common.validation.annotation.Validation;
import com.dfirago.authenticationapp.common.validation.rule.ValidationRule;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Dmytro Firago on 04/06/2017.
 */
public abstract class ReflectionUtils {

    public static List<Field> getControllerFields(final Class<?> controllerClass,
                                                  final Class<?> fieldClass) {
        final List<Field> controllerViewFields = new ArrayList<>();
        // Fields declared in the controller
        controllerViewFields.addAll(getFields(controllerClass, fieldClass));
        // Inherited fields
        Class<?> superClass = controllerClass.getSuperclass();
        while (!superClass.equals(Object.class)) {
            List<Field> viewFields = getFields(superClass, fieldClass);
            if (viewFields.size() > 0) {
                controllerViewFields.addAll(viewFields);
            }
            superClass = superClass.getSuperclass();
        }
        return controllerViewFields;
    }

    public static List<Field> getFields(final Class<?> targetClass,
                                        final Class<?> fieldClass) {
        final List<Field> viewFields = new ArrayList<>();
        final Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (fieldClass.isAssignableFrom(field.getType())) {
                viewFields.add(field);
            }
        }
        return viewFields;
    }

    public static View.OnClickListener getOnClickListener(final View view) {
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

    public static Method getOnClickListenerMethod(final View.OnClickListener existingListener) {
        final Class<?> listenerClass = existingListener.getClass();
        try {
            return listenerClass.getMethod("onClick", View.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to retrieve listener method", e);
        }
    }

    public static Method getOnFocusChangeListenerMethod(final View view) {
        final View.OnFocusChangeListener existingListener = view.getOnFocusChangeListener();
        if (existingListener != null) {
            final Class<?> listenerClass = existingListener.getClass();
            try {
                return listenerClass.getDeclaredMethod("onFocusChange", listenerClass);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unable to retrieve listener method", e);
            }
        }
        return null;
    }

    public static Object getFieldValue(final Field field, final Object target) {
        try {
            field.setAccessible(true);
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to retrieve field value: " + field, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationAttributeValue(final Annotation annotation,
                                                    final String attributeName,
                                                    final Class<T> attributeDataType) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        final Method attributeMethod = getAnnotationAttributeMethod(annotationType, attributeName);
        if (attributeMethod == null) {
            throw new IllegalStateException(
                    String.format("Cannot find attribute '%s' in annotation '%s'",
                            attributeName, annotationType.getName()));
        } else {
            try {
                final Object result = attributeMethod.invoke(annotation);
                return attributeDataType.isPrimitive() ?
                        (T) result : attributeDataType.cast(result);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Method getAnnotationAttributeMethod(
            final Class<? extends Annotation> annotationType,
            final String attributeName) {
        try {
            return annotationType.getMethod(attributeName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Field> getAnnotatedControllerFields(final Class<?> controllerClass,
                                                           final Class<? extends Annotation> annotationType) {
        final List<Field> annotatedControllerFields = new ArrayList<>();
        // Fields declared in the controller
        annotatedControllerFields.addAll(getAnnotatedFields(controllerClass, annotationType));
        // Inherited fields
        Class<?> superClass = controllerClass.getSuperclass();
        while (!superClass.equals(Object.class)) {
            List<Field> annotatedFields = getAnnotatedFields(superClass, annotationType);
            if (annotatedFields.size() > 0) {
                annotatedControllerFields.addAll(annotatedFields);
            }
            superClass = superClass.getSuperclass();
        }
        return annotatedControllerFields;
    }

    public static List<Field> getAnnotatedFields(final Class<?> targetClass,
                                                 final Class<? extends Annotation> annotationType) {
        final List<Field> getAnnotatedFields = new ArrayList<>();
        final Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (isAnnotationPresentRecursively(field, annotationType)) {
                getAnnotatedFields.add(field);
            }
        }
        return getAnnotatedFields;
    }

    public static boolean isAnnotationPresentRecursively(final AnnotatedElement annotatedElement,
                                                         final Class<? extends Annotation> annotationType) {
        if (annotatedElement.isAnnotationPresent(annotationType)) {
            return true;
        }
        for (Annotation annotation : annotatedElement.getAnnotations()) {
            if (!isInJavaLangAnnotationPackage(annotation)
                    && isAnnotationPresentRecursively(annotation.annotationType(), annotationType)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static Set<Annotation> getAnnotationsRecursively(final Annotation annotation,
                                                            final Class<? extends Annotation> targetType) {
        final Set<Annotation> result = new HashSet<>();
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        if (annotationType.isAnnotationPresent(targetType)) {
            result.add(annotation);
        } else {
            for (Annotation nestedAnnotation : annotationType.getAnnotations()) {
                if (!isInJavaLangAnnotationPackage(nestedAnnotation)) {
                    result.addAll(getAnnotationsRecursively(nestedAnnotation, targetType));
                }
            }
        }
        return result;
    }

    public static Set<Annotation> getAnnotationsRecursively(final AnnotatedElement annotatedElement,
                                                            final Class<? extends Annotation> targetType) {
        final Set<Annotation> result = new HashSet<>();
        for (Annotation annotation : annotatedElement.getAnnotations()) {
            result.addAll(getAnnotationsRecursively(annotation, targetType));
        }
        return result;
    }

    public static Class<? extends ValidationRule> getValidationRuleType(Annotation annotation) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        if (!annotationType.isAnnotationPresent(Validation.class)) {
            throw new IllegalArgumentException("Provided annotation is not a Validation!");
        }
        return annotationType.getAnnotation(Validation.class).value();
    }

    public static ValidationRule instantiateRule(final Class<? extends ValidationRule> ruleType,
                                                 final Annotation ruleAnnotation,
                                                 final Context context) {
        try {
            final Constructor<? extends ValidationRule> constructor = ruleType
                    .getDeclaredConstructor(Context.class, ruleAnnotation.annotationType());
            constructor.setAccessible(true);
            return constructor.newInstance(context, ruleAnnotation);
        } catch (NoSuchMethodException | IllegalAccessException
                | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Unable to instantiate rule: " + ruleType.getSimpleName(), e);
        }
    }

    private static boolean isInJavaLangAnnotationPackage(Annotation annotation) {
        return isInJavaLangAnnotationPackage(annotation.annotationType().getName());
    }

    private static boolean isInJavaLangAnnotationPackage(String annotationType) {
        return annotationType.startsWith("java.lang.annotation");
    }
}
