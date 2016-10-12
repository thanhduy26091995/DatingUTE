package com.itute.dating.sign_in.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.sign_in.model.Submitter;
import com.itute.dating.sign_in.view.SignInActivity;
import com.itute.dating.util.Constants;


import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 18/09/2016.
 */
public class LoginFacebookPresenter {

    private static String TAG = "FacebookLogin";
    private Submitter submitter;
    private SignInActivity view;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private CallbackManager callbackManager;
    boolean isExists = true;


    //constructor
    public LoginFacebookPresenter(SignInActivity view, FirebaseAuth mAuth, FirebaseAuth.AuthStateListener mAuthStateListener) {
        this.view = view;
        this.mAuth = mAuth;
        this.mAuthStateListener = mAuthStateListener;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new Submitter(mDatabase);

    }

    //đăng nhập thành công thì lưu user và chuyển tới MainActivity
    public void onAuthSuccess(final FirebaseUser user) {
        mDatabase.child(Constants.USERS).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    view.moveToMainActivity();
                } else {
                    long timestamp = new Date().getTime() / 1000;
                    Profile profile = Profile.getCurrentProfile();
                    submitter.addNewUser(user.getUid(), profile.getName(), profile.getProfilePictureUri(160, 160).toString(),
                            initAddressData(), "", 0, timestamp, "", "", "", "", "");
                    view.moveToMainActivity();
                }
                //update state login
                submitter.updateState(user.getUid(), true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

    private Map<String, Object> initAddressData() {
        Map<String, Object> address = new HashMap<String, Object>();
        address.put(Constants.ADDRESS, "");
        address.put(Constants.LATITUDE, 0);
        address.put(Constants.LONGITUDE, 0);
        return address;
    }

    //Login bằng Facebook
    public void setUpFacebook(Context context, CallbackManager callbackManager, final FirebaseAuth mAuth) {

        Log.d(TAG, "signWithFacebook");
        //khởi tạo Facebook SDK
        FacebookSdk.sdkInitialize(context);

        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        final LoginManager loginManager;
        loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(view, Arrays.asList(Constants.FACEBOOK_PERMISSION));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                //cập nhật lại accessToken
                accessToken.setCurrentAccessToken(loginResult.getAccessToken());
                handleFacebookAccessToken(loginResult.getAccessToken(), mAuth);
            }


            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel:");
                view.showToast("Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                view.showToast("Error");
                Log.d(TAG, error.getMessage());
            }
        });

    }

    //bắt đầu Auth với Facebook
    public void handleFacebookAccessToken(AccessToken accessToken, FirebaseAuth mAuth) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);
        view.showProgessDialog();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                //login fail
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    view.showToast("Authentication failed.");
                } else {

                    onAuthSuccess(task.getResult().getUser());

                }
                view.hideProgressDialog();
            }
        });
    }
}
