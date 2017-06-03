package com.dfirago.authenticationapp.common.auth;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;

/**
 * Created by dmfi on 01/06/2017.
 */

public class CognitoService {

    public Single<Boolean> authenticate(@NonNull String email, @NonNull String password) {
        // TODO
        return Single
                .just(email.equals("firago94@gmail.com") && password.equals("123"))
                .delay(1000, TimeUnit.MILLISECONDS);
    }
}
