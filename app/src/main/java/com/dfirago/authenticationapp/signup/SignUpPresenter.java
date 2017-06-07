package com.dfirago.authenticationapp.signup;

import com.dfirago.authenticationapp.BasePresenter;

/**
 * Created by Dmytro Firago on 06/06/2017.
 */
public class SignUpPresenter extends BasePresenter<SignUpView> {

    @Override
    protected Class viewClass() {
        return SignUpView.class;
    }
}
