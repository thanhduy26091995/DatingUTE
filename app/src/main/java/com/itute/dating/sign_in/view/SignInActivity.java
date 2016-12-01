package com.itute.dating.sign_in.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.itute.dating.main.view.MainActivity;
import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.base.view.GoogleAuthController;
import com.itute.dating.sign_in.presenter.LoginEmailPresenter;
import com.itute.dating.sign_in.presenter.LoginFacebookPresenter;
import com.itute.dating.sign_in.presenter.LoginGooglePresenter;
import com.itute.dating.util.Constants;
import com.itute.dating.util.EmailValidate;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 06/10/2016.
 */
public class SignInActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    public static String TAG = "SignInActivity";
    private LoginEmailPresenter emailPresenter;
    private LoginFacebookPresenter facebookPresenter;
    private LoginGooglePresenter googlePresenter;
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @BindView(R.id.edt_signin_email)
    EditText txtEmail;
    @BindView(R.id.edt_signin_password)
    EditText txtPassword;
    @BindView(R.id.txt_sign_up)
    TextView linkSignUp;

    @BindView(R.id.btn_sign_in)
    Button btnSignIn;
    @BindView(R.id.btn_facebook_login)
    ImageButton btnSignInWithFacebook;
    @BindView(R.id.btn_google_login)
    ImageButton btnSignInWithGoogle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        //init
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        initLogin();
        emailPresenter = new LoginEmailPresenter(mAuth, this);
        facebookPresenter = new LoginFacebookPresenter(this, mAuth, mAuthStateListener);
        googlePresenter = new LoginGooglePresenter(this);
        //event click
        btnSignIn.setOnClickListener(this);
        btnSignInWithFacebook.setOnClickListener(this);
        btnSignInWithGoogle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.txt_sign_up) {

        } else if (i == R.id.btn_sign_in) {
            signIn();
        } else if (i == R.id.btn_facebook_login) {
            signWithFacebook();
        } else if (i == R.id.btn_google_login) {
            signWithGoogle();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAuthController.install(this, this);
    }

    //ẩn bàn phím
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = txtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getResources().getString(R.string.batBuoc));
            valid = false;
        } else {
            txtEmail.setError(null);
        }

        String password = txtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError(getResources().getString(R.string.batBuoc));
            valid = false;
        } else {
            txtPassword.setError(null);
        }

        return valid;
    }

    //đăng nhập bằng email
    private void signIn() {
        if (!validateForm()) {
            return;
        }
        if (EmailValidate.IsOk(txtEmail.getText().toString())) {
            emailPresenter.signIn(txtEmail.getText().toString(), txtPassword.getText().toString());
        } else {
            Toast.makeText(this, getResources().getString(R.string.emailInvalid), Toast.LENGTH_SHORT).show();
        }
    }

    //đăng nhập bằng facebook
    private void signWithFacebook() {
        facebookPresenter.setUpFacebook(this, callbackManager, mAuth);
    }

    private void signWithGoogle() {
        googlePresenter.signIn();
    }

    //chuyển tới MainActivity
    public void moveToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //show thông báo cho người dùng
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //khởi tạo để tiến hành login
    public void initLogin() {

        // [START auth_state_listener]
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
    }

    //trả về kết quả sau khi chọn account trong Google Login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                googlePresenter.firebaseAuthWithGoogle(mAuth, this, account);
            } else {
                Log.d(TAG, "GoogleSubmitter login fail");
            }

        }
        if (requestCode != Constants.RC_SIGN_IN && resultCode == RESULT_OK) {

            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }
}
