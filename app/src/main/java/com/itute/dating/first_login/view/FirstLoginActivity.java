package com.itute.dating.first_login.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.base.view.GoogleAuthController;
import com.itute.dating.first_login.presenter.FirstLoginPresenter;
import com.itute.dating.main.view.MainActivity;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;
import com.itute.dating.util.EventDateTimeFormatter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 09/10/2016.
 */
public class FirstLoginActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.form_first_login_gender)
    LinearLayout formGender;
    @BindView(R.id.form_first_login_date)
    LinearLayout fromDate;
    @BindView(R.id.form_first_login_address)
    LinearLayout formAddress;
    @BindView(R.id.txt_first_login_gender)
    TextView txtGender;
    @BindView(R.id.btn_back)
    TextView txtBack;
    @BindView(R.id.txt_first_login_address)
    TextView txtAddress;
    @BindView(R.id.img_avatar_user)
    ImageView imgAvatar;
    @BindView(R.id.txt_first_login_date)
    TextView txtDate;
    @BindView(R.id.txt_submit_info)
    TextView txtSubmit;

    private String[] listGender;
    private double lat, lon;
    private FirstLoginPresenter presenter;
    private DatabaseReference mDatabase;
    private static final String TAG = "FirstLoginActivity";
    private long dateOfBirth = new Date().getTime();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);
        ButterKnife.bind(this);
        FacebookSdk.sdkInitialize(this);
        //init
        presenter = new FirstLoginPresenter(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        initInfo();
        //event click
        formGender.setOnClickListener(this);
        txtBack.setOnClickListener(this);
        formAddress.setOnClickListener(this);
        fromDate.setOnClickListener(this);
        txtSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.form_first_login_gender) {
            showPopUpGender();
        } else if (i == R.id.btn_back) {
           signOut();
        } else if (i == R.id.form_first_login_address) {
            presenter.showPlace();
        } else if (i == R.id.form_first_login_date) {
            presenter.onPickDateStart();
        } else if (i == R.id.txt_submit_info) {
            submitData();
        }
    }

    private void signOut() {
        if (GoogleAuthController.getInstance().getGoogleApiClient() != null) {
            presenter.logOut(GoogleAuthController.getInstance().getGoogleApiClient());
        }
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
    }
    private void initInfo() {
        try {
            mDatabase.child(Constants.USERS).child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            Glide.with(FirstLoginActivity.this)
                                    .load(user.getPhotoURL())
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .error(R.drawable.avatar)
                                    .centerCrop()
                                    .into(imgAvatar);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    //hiển thị thông báo
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void submitData() {
        boolean result = true;
        if (TextUtils.isEmpty(txtGender.getText().toString())) {
            result = false;
            showToast(getResources().getString(R.string.dienDayDuThongTin));
        }

        if (TextUtils.isEmpty(txtAddress.getText())) {
            result = false;
            showToast(getResources().getString(R.string.dienDayDuThongTin));
        }
        if (TextUtils.isEmpty(txtDate.getText())) {
            result = false;
            showToast(getResources().getString(R.string.dienDayDuThongTin));
        }
        if (result) {
            presenter.updateDataUser(getUid(), dataGender(txtGender.getText().toString()), dataAddress(), dateOfBirth);

        }
    }

    private int dataGender(String gender) {
        if (gender.equalsIgnoreCase("Nam")) {
            return 1;
        } else {
            return 2;
        }
    }

    private Map<String, Object> dataAddress() {
        Map<String, Object> data = new HashMap<>();
        data.put(Constants.ADDRESS, txtAddress.getText());
        data.put(Constants.LATITUDE, lat);
        data.put(Constants.LONGITUDE, lon);
        return data;
    }

    private void showPopUpGender() {
        listGender = getResources().getStringArray(R.array.arr_gender);
        createPopUp(listGender, getResources().getString(R.string.chonGioiTinh), txtGender);
    }

    private void createPopUp(final String[] values, String title, final TextView textToShow) {

        Dialog d = new AlertDialog.Builder(FirstLoginActivity.this, AlertDialog.THEME_HOLO_LIGHT)
                .setTitle(title)
                .setNegativeButton("Hủy Chọn", null)
                .setItems(values, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg, int position) {
                        textToShow.setText(values[position]);
                    }
                })
                .create();
        d.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String address = String.format("%s", place.getAddress());
                txtAddress.setText(address);
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateOfBirth = c.getTime().getTime();

        txtDate.setText(EventDateTimeFormatter.formatDateStart(dateOfBirth));
    }
}
