package com.itute.dating.settings.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;
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
import com.itute.dating.util.Constants;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.settings_1)
    LinearLayout settingGender;
    @BindView(R.id.txt_gender)
    TextView txtGender;
    @BindView(R.id.oldFromTo)
    TextView txtFromTo;


    private DatabaseReference mUserReference;
    private SettingsPresenter presenter;
    private static final String TAG = "SettingsFragment";
    private String[] listGender;
    private int initFrom, initTo;

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

        //event click
        btnSignOut.setOnClickListener(this);
        formProfile.setOnClickListener(this);
        settingGender.setOnClickListener(this);
        txtFromTo.setOnClickListener(this);
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
        } else if (i == R.id.settings_1) {
            openPopupGender();
        } else if (i == R.id.oldFromTo) {
            showDialog();
        }
    }

    private void showDialog() {
        final List<Integer> listAge = new ArrayList<>();
        listAge.add(0, initFrom);
        listAge.add(1, initTo);
        AlertDialog.Builder popupDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.custom_alertdialog_rangebar, null);
        popupDialog.setView(v);
        popupDialog.setTitle(getResources().getString(R.string.chooseAge));

        //event change
        com.appyvet.rangebar.RangeBar rangeBar = (com.appyvet.rangebar.RangeBar) v.findViewById(R.id.rangebar);
        rangeBar.setRangePinsByValue(initFrom, initTo);
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                listAge.set(0, Integer.parseInt(leftPinValue));
                listAge.set(1, Integer.parseInt(rightPinValue));
                //update initFrom, initTo
                initFrom = Integer.parseInt(leftPinValue);
                initTo = Integer.parseInt(rightPinValue);
            }
        });
        //popup event
        popupDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.updateAge(BaseActivity.getUid(), listAge.get(0), listAge.get(1));
                txtFromTo.setText(String.format("Từ %d đến %d", listAge.get(0), listAge.get(1)));
            }
        });
        popupDialog.create().show();

    }

    private void openPopupGender() {
        listGender = getResources().getStringArray(R.array.arr_gender_full);
        createPopUp(listGender, "Chọn giới tính", txtGender);
    }

    private int dataGender(String gender) {
        if (gender.equalsIgnoreCase("Nam")) {
            return 1;
        } else if (gender.equalsIgnoreCase("Nữ")) {
            return 2;
        } else return 0;
    }

    private void createPopUp(final String[] values, String title, final TextView textToShow) {

        Dialog d = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                .setTitle(title)
                .setNegativeButton("Hủy Chọn", null)
                .setItems(values, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        textToShow.setText(values[position]);
                        presenter.updateSearchGender(BaseActivity.getUid(), dataGender(values[position]));
                    }
                })
                .create();
        d.show();
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
                        int genderSearch = Integer.parseInt(user.getSearch().get(Constants.GENDER).toString());
                        if (genderSearch == 1) {
                            txtGender.setText("Nam");
                        } else if (genderSearch == 2) {
                            txtGender.setText("Nữ");
                        } else {
                            txtGender.setText("Cả hai");
                        }
                        //set value for int
                        initFrom = Integer.parseInt(user.getSearch().get(Constants.FROM).toString());
                        initTo = Integer.parseInt(user.getSearch().get(Constants.TO).toString());
                        txtFromTo.setText(String.format("Từ %d đến %d", Integer.parseInt(user.getSearch().get(Constants.FROM).toString()), Integer.parseInt(user.getSearch().get(Constants.TO).toString())));
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
