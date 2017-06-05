package com.dfirago.authenticationapp.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dfirago.authenticationapp.R;
import com.dfirago.authenticationapp.common.auth.CognitoService;
import com.dfirago.authenticationapp.common.validation.annotation.NotEmpty;
import com.dfirago.authenticationapp.common.validation.annotation.ValidationListener;
import com.dfirago.authenticationapp.common.validation.annotation.ValidationTrigger;
import com.dfirago.authenticationapp.common.validation.ValidationBinder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements LoginView {

    @NotEmpty
    @ValidationListener(onTextChanged = true)
    @BindView(R.id.email)
    protected EditText emailView;

    @NotEmpty
    @ValidationListener(onTextChanged = true)
    @BindView(R.id.password)
    protected EditText passwordView;

    @ValidationTrigger
    @BindView(R.id.sign_in_button)
    protected Button loginButton;

    private ProgressDialog progress;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ValidationBinder.bind(this);
        presenter = new LoginPresenter(new CognitoService());
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

    @OnClick(R.id.sign_in_button)
    protected void onLoginClick() {
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        presenter.authenticate(email, password);
    }

    @Override
    public void onAuthenticationStarted() {
        progress = ProgressDialog.show(this, "Authenticating...", null);
    }

    @Override
    public void onAuthenticationFinished() {
        progress.dismiss();
    }

    @Override
    public void onAuthenticationSuccess() {
        Toast.makeText(this, "Authentication success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailure() {
        Toast.makeText(this, "Authentication failure", Toast.LENGTH_SHORT).show();
    }
}

