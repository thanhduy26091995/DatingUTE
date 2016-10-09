package com.itute.dating.settings.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.itute.dating.base.view.GoogleAuthController;
import com.itute.dating.settings.view.SettingsFragment;

/**
 * Created by buivu on 10/10/2016.
 */
public class SettingsPresenter {
    private SettingsFragment view;

    public SettingsPresenter(SettingsFragment view) {
        this.view = view;
    }
    //log out facebook and google
    public void logOut(final GoogleApiClient mGoogleApiClient) {
        GoogleAuthController.getInstance().signOut(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                view.moveToLogin();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("Error", "GoogleSubmitter API Client Connection Suspended");
            }
        });
    }
}
