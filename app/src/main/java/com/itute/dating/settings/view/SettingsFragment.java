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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.base.view.GoogleAuthController;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.profile_user.view.ProfileUserActivity;
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
    @BindView(R.id.setting_avatar)
    ImageView imgAvatar;
    @BindView(R.id.settings_profile)
    TextView txtName;
    @BindView(R.id.settings_border)
    RelativeLayout formProfile;

    private com.appyvet.rangebar.RangeBar rangeBar;
    private DatabaseReference mUserReference;
    private SettingsPresenter presenter;
    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init
        presenter = new SettingsPresenter(this);
        mUserReference = FirebaseDatabase.getInstance().getReference();
        mUserReference = presenter.getUser(BaseActivity.getUid());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_settings, container, false);
        FacebookSdk.sdkInitialize(getContext());
        ButterKnife.bind(this, rootView);

        rangeBar = (com.appyvet.rangebar.RangeBar) rootView.findViewById(R.id.rangebar);
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                Log.d(TAG, "" + leftPinIndex + "/" + rightPinIndex + "/" + leftPinValue + "/" + rightPinValue);
            }
        });
        //event click
        btnSignOut.setOnClickListener(this);
        formProfile.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.setting_button) {
            signOut();
        } else if (i == R.id.settings_border) {
            Intent myIntent = new Intent(getActivity(), ProfileUserActivity.class);
            myIntent.putExtra(ProfileUserActivity.EXTRA_UID, BaseActivity.getUid());
            startActivity(myIntent);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initInfo();
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

    private void initInfo() {
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        txtName.setText(user.getDisplayName());
                        ImageLoader.getInstance().loadImage(getActivity(), user.getPhotoURL(), imgAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }
}
