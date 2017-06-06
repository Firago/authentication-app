package com.dfirago.authenticationapp.login;

import android.support.annotation.NonNull;

import com.dfirago.authenticationapp.BasePresenter;
import com.dfirago.authenticationapp.common.auth.google.FirebaseService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Dmytro Firago on 01/06/2017.
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    private FirebaseService firebaseService;

    public LoginPresenter(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public void authenticate(@NonNull String email, @NonNull String password) {
        firebaseService.authenticate(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> view().onAuthenticationStarted())
                .subscribe(user -> {
                    view().onAuthenticationFinished();
                    view().onAuthenticationSuccess();
                }, error -> {
                    view().onAuthenticationFinished();
                    view().onAuthenticationFailure();
                });
    }

    @Override
    protected Class viewClass() {
        return LoginView.class;
    }
}
