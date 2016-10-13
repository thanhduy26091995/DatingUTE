package com.itute.dating.profile_user.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.itute.dating.chat.view.ChatActivity;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.profile_user.presenter.ProfileUserPresenter;
import com.itute.dating.util.Constants;
import com.itute.dating.util.EventDateTimeFormatter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CheckedInputStream;

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
    @BindView(R.id.txt_chat)
    TextView txtChat;


    private DatabaseReference mUserReference;
    private ProfileUserPresenter presenter;
    private static final String TAG = "ProfileUserActivity";
    private String[] listGender, listJob, listReligion, listStar;
    private long dateOfBirth;
    private double lat, lon;
    private int yearOfBirth;
    private boolean isChangeDate;
    public static final String EXTRA_UID = "uid_key";
    private String intentUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        //butter knife
        ButterKnife.bind(this);
        //init
        presenter = new ProfileUserPresenter(this);
        mUserReference = FirebaseDatabase.getInstance().getReference();
        initViews();
        // Get uid from intent
        intentUid = getIntent().getStringExtra(EXTRA_UID);
        if (intentUid == null) {
            throw new IllegalArgumentException("Must pass UID");
        } else if (!intentUid.equalsIgnoreCase(getUid())) {
            disableViews();
        }
        //user ref
        mUserReference = presenter.getUser(intentUid);
        //
        initInfo();
    }

    private void disableViews() {
        txtSaveInfo.setVisibility(View.GONE);
        txtEditInfo.setVisibility(View.GONE);
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
        formPhone.setOnClickListener(this);
        formReligion.setOnClickListener(this);
        formLanguage.setOnClickListener(this);
        formHobby.setOnClickListener(this);
        imgAvatarUser.setOnClickListener(this);
        txtChat.setOnClickListener(this);
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
        } else if (i == R.id.form_profile_phone) {
            editPhone();
        } else if (i == R.id.form_profile_language) {
            editLanguage();
        } else if (i == R.id.form_profile_hobby) {
            editHobby();
        } else if (i == R.id.img_avatar_user) {
            openGallery();
        } else if (i == R.id.txt_save_info) {
            saveInfo();
        } else if (i == R.id.txt_chat) {
            moveToChatActivity();
        }
    }

    private void moveToChatActivity() {
        Intent myIntent = new Intent(ProfileUserActivity.this, ChatActivity.class);
        myIntent.putExtra(ChatActivity.PARTNER_ID, intentUid);
        startActivity(myIntent);
    }

    private void saveInfo() {
        try {
            boolean result = true;
            String name = edtName.getText().toString();
            int gender = intGender(txtGender.getText().toString());
            String phone = edtPhone.getText().toString();
            long dayOfBirth = dateOfBirth;
            String star = txtStar.getText().toString();
            String job = txtJob.getText().toString();
            String language = edtLanguage.getText().toString();
            String religion = txtReligion.getText().toString();
            String hobby = edtHobby.getText().toString();
            if (TextUtils.isEmpty(name)) {
                result = false;
                showToast(getResources().getString(R.string.tenKhongDuocDeTrong));
            }
            if (result) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);

                User user = new User();
                user.setDisplayName(name);
                user.setGender(gender);
                user.setPhone(phone);
                user.setDateOfBirth(dayOfBirth);
                user.setStar(star);
                user.setJob(job);
                user.setLanguage(language);
                user.setReligion(religion);
                user.setHobby(hobby);
                user.setAddress(setLocation());

                user.setOld(currentYear - yearOfBirth);

                //edit user data
                presenter.editUser(user, getUid());
                //hide txtSave, show txtEdit
                txtEditInfo.setVisibility(View.VISIBLE);
                txtSaveInfo.setVisibility(View.GONE);
                //enable = false
                edtHobby.setEnabled(false);
                edtName.setEnabled(false);
                edtLanguage.setEnabled(false);
                edtPhone.setEnabled(false);
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private Map<String, Object> setLocation() {
        Map<String, Object> location = new HashMap<>();
        location.put(Constants.ADDRESS, txtLocation.getText().toString());
        location.put(Constants.LATITUDE, lat);
        location.put(Constants.LONGITUDE, lon);
        return location;
    }

    private int intGender(String gender) {
        if (gender.equalsIgnoreCase("Nam")) {
            return 1;
        } else {
            return 2;
        }
    }

    private void openGallery() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            presenter.showImage();
        }
    }

    private void editHobby() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            if (!edtHobby.isEnabled()) {
                edtHobby.setEnabled(true);
                edtHobby.setFocusable(true);
                edtHobby.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    private void editLanguage() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            if (!edtLanguage.isEnabled()) {
                edtLanguage.setEnabled(true);
                edtLanguage.setFocusable(true);
                edtLanguage.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    private void editPhone() {
        if (txtSaveInfo.getVisibility() == View.VISIBLE) {
            if (!edtPhone.isEnabled()) {
                edtPhone.setEnabled(true);
                edtPhone.setFocusable(true);
                edtPhone.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
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
                        lat = Double.parseDouble(user.getAddress().get(Constants.LATITUDE).toString());
                        lon = Double.parseDouble(user.getAddress().get(Constants.LONGITUDE).toString());
                        dateOfBirth = user.getDateOfBirth();
                        txtCalendar.setText(EventDateTimeFormatter.formatDateStart(dateOfBirth));
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(dateOfBirth);
                        yearOfBirth = c.get(Calendar.YEAR);
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
        isChangeDate = true;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateOfBirth = c.getTime().getTime();
        yearOfBirth = year;
        txtCalendar.setText(EventDateTimeFormatter.formatDateStart(dateOfBirth));
        //tính toán cung hoàng đạo

    }

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
        } else if (requestCode == Constants.GALLERY_INTENT && resultCode == RESULT_OK) {
            ImageLoader.getInstance().loadImage(ProfileUserActivity.this, data.getData().toString(), imgAvatarUser);
            Constants.USER_FILE_PATH = getRealPathFromURI(data.getData());
            Log.d("URL", "" + data.getData());
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    //hiển thị thông báo
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
