package com.dfirago.authenticationapp.login;

/**
 * Created by dmfi on 01/06/2017.
 */

public interface LoginView {

    void onAuthenticationStarted();

    void onAuthenticationFinished();

    void onAuthenticationSuccess();

    void onAuthenticationFailure();
}
