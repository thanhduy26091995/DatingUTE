package com.itute.dating.profile_user.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.add_image.view.AddImageActivity;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.base.view.GoogleAuthController;
import com.itute.dating.chat.view.ChatActivity;
import com.itute.dating.list_heart.view.HeartActivity;
import com.itute.dating.maps.view.MapsActivity;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.profile_user.presenter.ProfileUserPresenter;
import com.itute.dating.util.Constants;
import com.itute.dating.util.EventDateTimeFormatter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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
    @BindView(R.id.txt_heart)
    TextView txtHeart;
    @BindView(R.id.status)
    EditText edtStatus;
    @BindView(R.id.txt_edit_status)
    TextView txtEditStatus;
    @BindView(R.id.txt_submit_status)
    TextView txtSubmitStatus;
    @BindView(R.id.txt_gallery)
    TextView txtGallery;
    @BindView(R.id.txt_distance)
    TextView txtDistance;

    private DatabaseReference mUserReference;
    private ProfileUserPresenter presenter;
    private static final String TAG = "ProfileUserActivity";
    private String[] listGender, listJob, listReligion, listStar;
    private long dateOfBirth;
    private double lat, lon;
    private String avatarUrl, address;
    private int yearOfBirth;
    private boolean isChangeDate;
    public static final String EXTRA_UID = "uid_key";
    private String intentUid;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private static final int REQUEST_CODE_LOCATION = 1001;
    private static final String[] PERMISSION_GROUP = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

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
        mGoogleApiClient = GoogleAuthController.getInstance().getGoogleApiClient();
        initInfo();


    }

    private boolean verifyLocationPermission() {
        int locationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, PERMISSION_GROUP, REQUEST_CODE_LOCATION);
            return false;
        }
        return true;
    }

    private void showSettingLocationAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set title
        builder.setTitle(getResources().getString(R.string.GPSTitle));
        //set message
        builder.setMessage(getResources().getString(R.string.GPSContent));
        //on press
        builder.setPositiveButton(getResources().getString(R.string.setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent settingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(settingIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        //on cancel
        builder.setNegativeButton(getResources().getString(R.string.huy), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayLocation();
                }
                break;
        }
    }

    private void displayLocation() {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Location currentLocation = new Location("srcLocation");
                double currentLat = mLastLocation.getLatitude();
                double currentLon = mLastLocation.getLongitude();
                currentLocation.setLatitude(currentLat);
                currentLocation.setLongitude(currentLon);


                Location desLocation = new Location("desLocation");
                desLocation.setLatitude(lat);
                desLocation.setLongitude(lon);

                float distance = currentLocation.distanceTo(desLocation) / 1000;
                txtDistance.setText(String.format("%.2f km", distance));
                Log.d(TAG, "" + currentLat + "/" + currentLon);
            } else {
                Log.d(TAG, "0: 0");
                txtDistance.setText("N/A");
                //showSettingLocationAlert();
            }
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void disableViews() {
        txtSaveInfo.setVisibility(View.GONE);
        txtEditInfo.setVisibility(View.GONE);
        txtEditStatus.setVisibility(View.GONE);
        txtSubmitStatus.setVisibility(View.GONE);
        txtHeart.setVisibility(View.GONE);
    }

    private void initViews() {
        //default
        txtSaveInfo.setVisibility(View.GONE);
        txtEditInfo.setVisibility(View.VISIBLE);
        txtEditStatus.setVisibility(View.VISIBLE);
        txtSubmitStatus.setVisibility(View.GONE);
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
        txtHeart.setOnClickListener(this);
        txtEditStatus.setOnClickListener(this);
        txtSubmitStatus.setOnClickListener(this);
        txtGallery.setOnClickListener(this);
        txtLocation.setOnClickListener(this);
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
        if (edtStatus.isEnabled()) {
            edtStatus.setEnabled(false);
        }

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btnAppBack) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (i == R.id.form_profile_gender) {
            showPopUpGender();
        } else if (i == R.id.txt_edit_info) {
            editInfo();
        } else if (i == R.id.form_profile_calendar) {
            openCalendar();
        } else if (i == R.id.txt_profile_location) {
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
        } else if (i == R.id.txt_heart) {
            moveToHeartActivity();
        } else if (i == R.id.txt_edit_status) {
            editStatus();
        } else if (i == R.id.txt_submit_status) {
            submitStatus();
        } else if (i == R.id.txt_gallery) {
            moveToAddImageGallery();
        }
    }


    private void openGoogleMap() {
        if (!intentUid.equals(getUid())) {
//            Uri intentMapUri = Uri.parse(String.format(Locale.ENGLISH, "http://maps.google.com/maps?&daddr=%f,%f", lat, lon));
//            Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentMapUri);
//            mapIntent.setPackage("com.google.android.apps.maps");
//            startActivity(mapIntent);
            Intent myIntent = new Intent(ProfileUserActivity.this, MapsActivity.class);
            Log.d(TAG, "" + lat + "/" + lon);
            myIntent.putExtra(MapsActivity.LAT, lat);
            myIntent.putExtra(MapsActivity.LON, lon);
            myIntent.putExtra(MapsActivity.AVATAR_URL, avatarUrl);
            myIntent.putExtra(MapsActivity.ADDRESS, address);
            startActivity(myIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    private void moveToAddImageGallery() {
        Intent myIntent = new Intent(ProfileUserActivity.this, AddImageActivity.class);
        myIntent.putExtra(AddImageActivity.EXTRA_UID, intentUid);
        startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void submitStatus() {
        //cập nhật status
        presenter.updateStatus(edtStatus.getText().toString(), intentUid);
        txtEditStatus.setVisibility(View.VISIBLE);
        txtSubmitStatus.setVisibility(View.GONE);
        edtStatus.setEnabled(false);
    }

    private void editStatus() {
        txtEditStatus.setVisibility(View.GONE);
        txtSubmitStatus.setVisibility(View.VISIBLE);
        if (txtSubmitStatus.getVisibility() == View.VISIBLE) {
            if (!edtStatus.isEnabled()) {
                edtStatus.setEnabled(true);
                edtStatus.setFocusable(true);
                edtStatus.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    private void moveToChatActivity() {
        Intent myIntent = new Intent(ProfileUserActivity.this, ChatActivity.class);
        myIntent.putExtra(ChatActivity.PARTNER_ID, intentUid);
        startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void moveToHeartActivity() {
        Intent myIntent = new Intent(ProfileUserActivity.this, HeartActivity.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        if (intentUid.equals(getUid())) {
            if (txtSaveInfo.getVisibility() == View.VISIBLE) {
                presenter.showPlace();

            }
        } else {
            openGoogleMap();
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
                        edtStatus.setText(user.getStatus());
                        txtJob.setText(user.getJob());
                        txtReligion.setText(user.getReligion());
                        txtStar.setText(user.getStar());
                        lat = Double.parseDouble(user.getAddress().get(Constants.LATITUDE).toString());
                        lon = Double.parseDouble(user.getAddress().get(Constants.LONGITUDE).toString());
                        avatarUrl = user.getPhotoURL();
                        address = user.getAddress().get(Constants.ADDRESS).toString();
                        dateOfBirth = user.getDateOfBirth();
                        txtCalendar.setText(EventDateTimeFormatter.formatDateStart(dateOfBirth));
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(dateOfBirth);
                        yearOfBirth = c.get(Calendar.YEAR);
                        //load avatar
                        ImageLoader.getInstance().loadImage(ProfileUserActivity.this, user.getPhotoURL(), imgAvatarUser);
                        //distance from current location to destination location
                        if (verifyLocationPermission()) {
                            displayLocation();
                        }
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
