package com.dfirago.authenticationapp.signup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.dfirago.authenticationapp.R;
import com.dfirago.authenticationapp.common.validation.ValidationBinder;
import com.dfirago.authenticationapp.common.validation.annotation.Email;
import com.dfirago.authenticationapp.common.validation.annotation.Equals;
import com.dfirago.authenticationapp.common.validation.annotation.Password;
import com.dfirago.authenticationapp.common.validation.annotation.ValidationListener;
import com.dfirago.authenticationapp.common.validation.annotation.ValidationTrigger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Dmytro Firago on 06/06/2017.
 */
public class SignUpActivity extends AppCompatActivity implements SignUpView {

    @Email
    @ValidationListener(onFocusLost = true)
    @BindView(R.id.email)
    protected EditText emailView;

    @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC_MIXED_CASE)
    @ValidationListener(onFocusLost = true)
    @BindView(R.id.password)
    protected EditText passwordView;

    @Equals(property = "passwordView", messageResId = R.string.error_confirm_password)
    @ValidationListener(onFocusLost = true)
    @BindView(R.id.confirm_password)
    protected EditText confirmPasswordView;

    @ValidationTrigger
    @BindView(R.id.sign_up_button)
    protected Button signUpButton;

    private ProgressDialog progress;
    private SignUpPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        ValidationBinder.bind(this);
        presenter = new SignUpPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(this);
    }

    @Override
    protected void onPause() {
        presenter.detachView();
        super.onPause();
    }
}
