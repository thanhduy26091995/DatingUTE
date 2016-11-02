package com.itute.dating.sign_in.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.base.view.GoogleAuthController;
import com.itute.dating.sign_in.model.Submitter;
import com.itute.dating.sign_in.view.SignInActivity;
import com.itute.dating.util.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 08/10/2016.
 */
public class LoginGooglePresenter {
    private SignInActivity view;
    private Submitter submitter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static String TAG = "GoogleLogin";

    //constructor
    public LoginGooglePresenter(SignInActivity view) {
        this.view = view;

        mDatabase = FirebaseDatabase.getInstance().getReference();

        submitter = new Submitter(mDatabase);
    }

    //hàm đăng nhập
    public void signIn() {
        GoogleAuthController.getInstance().signIn(view);

    }

    private Map<String, Object> initAddressData() {
        Map<String, Object> address = new HashMap<String, Object>();
        address.put(Constants.ADDRESS, "");
        address.put(Constants.LATITUDE, 0);
        address.put(Constants.LONGITUDE, 0);
        return address;
    }

    private Map<String, Object> initSearchData() {
        Map<String, Object> address = new HashMap<String, Object>();
        address.put(Constants.GENDER, 0);
        address.put(Constants.FROM, 16);
        address.put(Constants.TO, 30);
        return address;
    }

    //đăng nhập thành công thì lưu user và chuyển tới MainActivity
    public void onAuthSuccess(final FirebaseUser user, final GoogleSignInAccount acct) {

        mDatabase.child(Constants.USERS).child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    view.moveToMainActivity();
                } else {
                    try {
                        long timestamp = new Date().getTime() / 1000;
                        submitter.addNewUser(user.getUid(), acct.getDisplayName(), acct.getPhotoUrl().toString(), initAddressData(),
                                "", 0, timestamp, "", "", "", "", "", initSearchData());
                        view.moveToMainActivity();
                    } catch (Exception e) {
                        Log.d("onAuthSuccessGoogle", "" + e.getMessage());
                    }
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

    //auth với Google, nếu Auth thành công thì gọi hàm onAuthSuccess
    public void firebaseAuthWithGoogle(FirebaseAuth mAuth, Activity activity, final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        view.showProgessDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    view.showToast("Authentication failed.");

                } else {

                    onAuthSuccess(task.getResult().getUser(), acct);
                }
                view.hideProgressDialog();
            }
        });
    }
    // [END auth_with_google]

}
