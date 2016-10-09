package com.itute.dating.settings.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.itute.dating.R;
import com.itute.dating.base.view.GoogleAuthController;
import com.itute.dating.settings.presenter.SettingsPresenter;
import com.itute.dating.sign_in.view.SignInActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 09/10/2016.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.setting_button)
    Button btnSignOut;


    private SettingsPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init
        presenter = new SettingsPresenter(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_settings, container, false);
        FacebookSdk.sdkInitialize(getContext());
        ButterKnife.bind(this, rootView);
        //event click
        btnSignOut.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.setting_button) {
            signOut();
        }
    }

    private void signOut() {
        if (GoogleAuthController.getInstance().getGoogleApiClient() != null) {
            presenter.logOut(GoogleAuthController.getInstance().getGoogleApiClient());
        }
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();

    }

    public void moveToLogin() {
        try {
            Intent intent = new Intent(getActivity(), SignInActivity.class);
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            Log.d("onMoveToLogin", "" + e.getMessage());
        }
    }
}
