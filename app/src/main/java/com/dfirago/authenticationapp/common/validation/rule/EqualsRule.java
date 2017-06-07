package com.dfirago.authenticationapp.common.validation.rule;

import android.content.Context;
import android.widget.TextView;

import com.dfirago.authenticationapp.common.validation.ValidationContext;
import com.dfirago.authenticationapp.common.validation.annotation.Equals;
import com.dfirago.authenticationapp.common.validation.utils.ReflectionUtils;

/**
 * Created by Dmytro Firago on 07/06/2017.
 */

public class EqualsRule extends ValidationRule<Equals, String> {

    protected EqualsRule(Context context, Equals annotation) {
        super(context, annotation);
    }

    @Override
    public boolean validate(final ValidationContext validationContext, final String selfData) {
        final Object controller = validationContext.getController();
        try {
            final Object property = ReflectionUtils
                    .getControllerProperty(controller, annotation.property());
            // TODO data adapters!!!
            if (TextView.class.isAssignableFrom(property.getClass())) {
                String otherData = ((TextView) property).getText().toString();
                return selfData.equals(otherData);
            } else {
                throw new IllegalArgumentException(annotation + " cannot be applied to property " + property);
            }
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Controller class does not have field: " + annotation.property());
        }
    }
}
