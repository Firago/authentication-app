package com.dfirago.authenticationapp;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Proxy;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by dmfi on 01/06/2017.
 */

public abstract class BasePresenter<View> {

    protected final CompositeDisposable disposables = new CompositeDisposable();

    @Nullable
    private View view;

    protected abstract Class viewClass();

    @CallSuper
    public void attachView(@NonNull View view) {
        this.view = view;
    }

    @NonNull
    protected View view() {
        return view;
    }

    @SuppressWarnings("unchecked")
    private View emptyView() {
        final Class viewClass = viewClass();
        return (View) Proxy.newProxyInstance(viewClass.getClassLoader(),
                new Class[]{viewClass}, (proxy, method, args) -> {
                    Log.d(getClass().getSimpleName(), "Empty view instance.");
                    return null;
                });
    }

    @CallSuper
    public void detachView() {
        disposables.dispose();
        view = emptyView();
    }
}