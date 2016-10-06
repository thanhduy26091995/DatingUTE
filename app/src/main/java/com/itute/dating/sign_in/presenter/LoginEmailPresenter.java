package com.itute.dating.sign_in.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.itute.dating.R;
import com.itute.dating.sign_in.view.SignInActivity;

/**
 * Created by buivu on 06/10/2016.
 */
public class LoginEmailPresenter {
    private static final String TAG = "LoginEmailPresenter";
    private FirebaseAuth mAuth;
    private SignInActivity view;

    public LoginEmailPresenter(FirebaseAuth mAuth, SignInActivity view) {
        this.mAuth = mAuth;
        this.view = view;
    }

    public void signIn(String email, String password) {
        try {
            view.showProgessDialog();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                    if (task.isSuccessful()) {
                        view.moveToMainActivity();
                    } else {
                        view.showToast(view.getResources().getString(R.string.dangNhapKhongThanhCong));
                    }
                    view.hideProgressDialog();
                }
            });
        } catch (Exception e) {
            Log.d(view.TAG, e.getMessage());
        }
    }

}
