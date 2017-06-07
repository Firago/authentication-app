package com.dfirago.authenticationapp.common.validation.rule;

import android.content.Context;

import com.dfirago.authenticationapp.common.validation.ValidationContext;
import com.dfirago.authenticationapp.common.validation.annotation.Password;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dmytro Firago on 06/06/2017.
 */
public class PasswordRule extends ValidationRule<Password, String> {

    private final Map<Password.Scheme, String> SCHEME_PATTERNS =
            new HashMap<Password.Scheme, String>() {
                {
                    put(Password.Scheme.ANY, ".+");
                    put(Password.Scheme.ALPHA, "\\w+");
                    put(Password.Scheme.ALPHA_MIXED_CASE, "(?=.*[a-z])(?=.*[A-Z]).+");
                    put(Password.Scheme.NUMERIC, "\\d+");
                    put(Password.Scheme.ALPHA_NUMERIC, "(?=.*[a-zA-Z])(?=.*[\\d]).+");
                    put(Password.Scheme.ALPHA_NUMERIC_MIXED_CASE,
                            "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]).+");
                    put(Password.Scheme.ALPHA_NUMERIC_SYMBOLS,
                            "(?=.*[a-zA-Z])(?=.*[\\d])(?=.*([^\\w])).+");
                    put(Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS,
                            "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*([^\\w])).+");
                }
            };

    protected PasswordRule(Context context, Password annotation) {
        super(context, annotation);
    }

    @Override
    public boolean validate(final ValidationContext validationContext, final String password) {
        boolean hasMinChars = password.length() >= annotation.min();
        boolean matchesScheme = password.matches(SCHEME_PATTERNS.get(annotation.scheme()));
        return hasMinChars && matchesScheme;
    }
}
