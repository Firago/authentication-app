package com.dfirago.authenticationapp.common.auth.google;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.Single;

/**
 * Created by Dmytro Firago on 06/06/2017.
 */
public class FirebaseService {

    private final FirebaseAuth firebaseAuth;

    public FirebaseService() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public Single<FirebaseUser> authenticate(String email, String password) {
        return Single.create(emitter -> firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> emitter.onSuccess(authResult.getUser()))
                .addOnFailureListener(emitter::onError)
        );
    }
}
