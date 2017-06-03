package com.dfirago.authenticationapp.login;

import android.support.annotation.NonNull;

import com.dfirago.authenticationapp.BasePresenter;
import com.dfirago.authenticationapp.common.auth.CognitoService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Dmytro Firago on 01/06/2017.
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private CognitoService cognitoService;

    public LoginPresenter(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    public void authenticate(@NonNull String email, @NonNull String password) {
        cognitoService.authenticate(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> view().onAuthenticationStarted())
                .subscribe(result -> {
                    if (result) {
                        view().onAuthenticationFinished();
                        view().onAuthenticationSuccess();
                    } else {
                        view().onAuthenticationFinished();
                        view().onAuthenticationFailure();
                    }
                });
    }

    @Override
    protected Class viewClass() {
        return LoginView.class;
    }
}
