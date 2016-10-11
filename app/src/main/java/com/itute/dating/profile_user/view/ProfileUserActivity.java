package com.itute.dating.profile_user.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.profile_user.presenter.ProfileUserPresenter;
import com.itute.dating.util.Constants;
import com.itute.dating.util.EventDateTimeFormatter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 11/10/2016.
 */
public class ProfileUserActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.btnAppBack)
    TextView btnBack;
    @BindView(R.id.img_avatar_user)
    ImageView imgAvatarUser;
    @BindView(R.id.txt_profile_gender)
    TextView txtGender;
    @BindView(R.id.edt_profile_phone)
    EditText edtPhone;
    @BindView(R.id.txt_profile_calendar)
    TextView txtCalendar;
    @BindView(R.id.txt_profile_star)
    TextView txtStar;
    @BindView(R.id.txt_profile_location)
    TextView txtLocation;
    @BindView(R.id.txt_profile_job)
    TextView txtJob;
    @BindView(R.id.edt_profile_language)
    EditText edtLanguage;
    @BindView(R.id.txt_profile_religion)
    TextView txtReligion;
    @BindView(R.id.edt_profile_hobby)
    EditText edtHobby;
    @BindView(R.id.edt_profile_usename)
    EditText edtName;
    @BindView(R.id.form_profile_gender)
    LinearLayout formGender;
    @BindView(R.id.txt_save_info)
    TextView txtSaveInfo;
    @BindView(R.id.txt_edit_info)
    TextView txtEditInfo;
    @BindView(R.id.form_profile_calendar)
    LinearLayout formCalendar;
    @BindView(R.id.form_profile_hobby)
    LinearLayout formHobby;
    @BindView(R.id.form_profile_job)
    LinearLayout formJob;
    @BindView(R.id.form_profile_language)
    LinearLayout formLanguage;
    @BindView(R.id.form_profile_location)
    LinearLayout formLocation;
    @BindView(R.id.form_profile_name)
    LinearLayout formName;
    @BindView(R.id.form_profile_religion)
    LinearLayout formReligion;
    @BindView(R.id.form_profile_star)
    LinearLayout formStar;
    @BindView(R.id.form_profile_phone)
    LinearLayout formPhone;


    private DatabaseReference mUserReference;
    private ProfileUserPresenter presenter;
    private static final String TAG = "ProfileUserActivity";
    private String[] listGender, listJob, listReligion, listStar;
    private long dateOfBirth;
    private double lat, lon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        //butter knife
        ButterKnife.bind(this);
        //init
        presenter = new ProfileUserPresenter(this);
        mUserReference = FirebaseDatabase.getInstance().getReference();
        mUserReference = presenter.getUser(getUid());

        //
        initViews();
        initInfo();
    }

    private void initViews() {
        //default
        txtSaveInfo.setVisibility(View.GONE);
        txtEditInfo.setVisibility(View.VISIBLE);
        //event click
        btnBack.setOnClickListener(this);
        formGender.setOnClickListener(this);
        txtSaveInfo.setOnClickListener(this);
        txtEditInfo.setOnClickListener(this);
        formCalendar.setOnClickListener(this);
        formLocation.setOnClickListener(this);
        formJob.setOnClickListener(this);
        formReligion.setOnClickListener(this);
        formStar.setOnClickListener(this);
        formName.setOnClickListener(this);
        edtName.setOnClickListener(this);
        //block editText
        if (edtName.isEnabled()) {
            edtName.setEnabled(false);
        }
        if (edtLanguage.isEnabled()) {
            edtLanguage.setEnabled(false);
        }
        if (edtHobby.isEnabled()) {
            edtHobby.setEnabled(false);
        }
        if (edtPhone.isEnabled()) {
            edtPhone.setEnabled(false);
        }

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btnAppBack) {
            finish();
        } else if (i == R.id.form_profile_gender) {
            showPopUpGender();
        } else if (i == R.id.txt_edit_info) {
            editInfo();
        } else if (i == R.id.form_profile_calendar) {
            openCalendar();
        } else if (i == R.id.form_profile_location) {
            openGooglePlace();
        } else if (i == R.id.form_profile_job) {
            showPopUpJob();
        } else if (i == R.id.form_profile_religion) {
            showPopUpReligion();
        } else if (i == R.id.form_profile_star) {
            showPopUpStar();
        } else if (i == R.id.form_profile_name) {
            editName();
        }
    }

    private void editName() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            if (!edtName.isEnabled()) {
                edtName.setEnabled(true);
                edtName.setFocusable(true);
                edtName.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    private void openGooglePlace() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            presenter.showPlace();
        }
    }

    private void openCalendar() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            presenter.onPickDateStart();
        }
    }

    private void editInfo() {
        //hide txtedit, show txtSave
        txtEditInfo.setVisibility(View.GONE);
        txtSaveInfo.setVisibility(View.VISIBLE);
    }

    private void createPopUp(final String[] values, String title, final TextView textToShow) {

        Dialog d = new AlertDialog.Builder(ProfileUserActivity.this, AlertDialog.THEME_HOLO_LIGHT)
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

    private void showPopUpJob() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            listJob = getResources().getStringArray(R.array.arr_job);
            createPopUp(listJob, "Chọn nghề nghiệp", txtJob);
        }
    }

    private void showPopUpGender() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            listGender = getResources().getStringArray(R.array.arr_gender);
            createPopUp(listGender, "Chọn giới tính", txtGender);
        }
    }

    private void showPopUpReligion() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            listReligion = getResources().getStringArray(R.array.arr_religion);
            createPopUp(listReligion, "Chọn tôn giáo", txtReligion);
        }
    }

    private void showPopUpStar() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            listStar = getResources().getStringArray(R.array.arr_star);
            createPopUp(listStar, "Chọn cung hoàng đạo", txtStar);
        }
    }

    private void initInfo() {
        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        edtName.setText(user.getDisplayName());
                        if (user.getGender() == 1) {
                            txtGender.setText("Nam");
                        } else {
                            txtGender.setText("Nữ");
                        }
                        edtPhone.setText(user.getPhone());
                        txtCalendar.setText(EventDateTimeFormatter.formatDateStart(user.getDateOfBirth()));
                        txtLocation.setText(user.getAddress().get(Constants.ADDRESS).toString());
                        edtHobby.setText(user.getHobby());
                        edtLanguage.setText(user.getLanguage());
                        txtJob.setText(user.getJob());
                        txtReligion.setText(user.getReligion());
                        txtStar.setText(user.getStar());
                        //load avatar
                        ImageLoader.getInstance().loadImage(ProfileUserActivity.this, user.getPhotoURL(), imgAvatarUser);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateOfBirth = c.getTime().getTime();
        txtCalendar.setText(EventDateTimeFormatter.formatDateStart(dateOfBirth));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String address = String.format("%s", place.getAddress());
                txtLocation.setText(address);
                lat = place.getLatLng().latitude;
                lon = place.getLatLng().longitude;
            }
        }
    }
}
