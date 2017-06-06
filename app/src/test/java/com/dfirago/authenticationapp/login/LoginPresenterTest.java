package com.dfirago.authenticationapp.login;

import com.dfirago.authenticationapp.common.auth.google.FirebaseService;
import com.google.firebase.auth.FirebaseUser;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Created by Dmytro Firago on 01/06/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock
    private FirebaseService firebaseService;
    @Mock
    private FirebaseUser firebaseUser;
    @Mock
    private LoginView loginView;

    private LoginPresenter loginPresenter;

    @BeforeClass
    public static void setUpOnce() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
    }

    @Before
    public void setUp() throws Exception {
        loginPresenter = new LoginPresenter(firebaseService);
        loginPresenter.attachView(loginView);
    }

    @Test
    public void authenticate_success() throws Exception {
        when(firebaseService.authenticate("login", "password"))
                .thenReturn(Single.just(firebaseUser));
        loginPresenter.authenticate("login", "password");
        InOrder loginViewOrder = inOrder(loginView);
        loginViewOrder.verify(loginView, times(1)).onAuthenticationStarted();
        loginViewOrder.verify(loginView, times(1)).onAuthenticationFinished();
        loginViewOrder.verify(loginView, times(1)).onAuthenticationSuccess();
    }

    @AfterClass
    public static void tearDownOnce() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }
}