package com.itute.dating.chat.view;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.chat.model.ChatMessage;
import com.itute.dating.chat.model.ChatMessageViewHolder;
import com.itute.dating.chat.presenter.ChatMessagePresenter;
import com.itute.dating.notification.PushMessage;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;


/**
 * Created by buivu on 12/10/2016.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    @BindView(R.id.et_message)
    EditText edtMessage;
    @BindView(R.id.rc_chat)
    RecyclerView mRecycler;
    @BindView(R.id.btn_confirm)
    TextView txtSend;
    @BindView(R.id.txt_partner_username)
    TextView txtPartnerName;
    //    @BindView(R.id.txt_partner_old)
//    TextView txtOld;
    @BindView(R.id.txt_partner_address)
    TextView txtAddress;
    //    @BindView(R.id.iv_avatar)
//    ImageView imgAvatarPartner;
    //        @BindView(R.id.txt_smile)
//    TextView txtIcon;
    @BindView(R.id.emojicons)
    FrameLayout frameIcon;
    @BindView(R.id.root_view)
    View rootView;
    @BindView(R.id.txt_micro)
    TextView txtMicro;
    @BindView(R.id.txt_gallery)
    TextView txtGallery;
    @BindView(R.id.txt_camera)
    TextView txtCamera;
    @BindView(R.id.txt_record)
    TextView txtRecord;
    @BindView(R.id.img_record)
    ImageView imgRecord;
    @BindView(R.id.emoji_btn)
    ImageView emojiImageView;


    private ChatMessagePresenter presenter;
    private FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> mAdapter;
    public static final String PARTNER_ID = "partnerId";
    public static final String TAG = "ChatActivity";
    private String partnerID;
    private DatabaseReference mUserPartnerReference, mUserCurrentReference;
    private String fromName, toName;
    private LinearLayoutManager mManager;
    private static final String PERMISSION_GROUP[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_PERMISSION = 85;
    private MediaRecorder mediaRecorder;
    private String audioSavePathInDevice = null;
    private MediaPlayer mediaPlayer;
    private StorageReference mStorage;

    //use for push notification
    private String deviceToken, currentAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

//        rootView = findViewById(R.id.root_view);
//        emojiImageView = (ImageView) findViewById(R.id.emoji_btn);
//        edtMessage = (EmojiconEditText) findViewById(R.id.et_message);
//        //init emojiIcon
//        emojIcon = new EmojIconActions(this, rootView, edtMessage, emojiImageView);
//        emojIcon.ShowEmojIcon();
//        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);

        //init firebase storage
        mStorage = FirebaseStorage.getInstance().getReference();
        //cache recycler view
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemViewCacheSize(100);
        mRecycler.setDrawingCacheEnabled(true);
        mRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //get intent
        partnerID = getIntent().getStringExtra(PARTNER_ID);
        //init firebase
        presenter = new ChatMessagePresenter(this);
        mUserPartnerReference = FirebaseDatabase.getInstance().getReference();
        mUserCurrentReference = FirebaseDatabase.getInstance().getReference();
        mUserCurrentReference = presenter.getUser(getUid());
        mUserPartnerReference = presenter.getUser(partnerID);
        //end init firebase
        mManager = new LinearLayoutManager(this);
        //load data tin nhắn
        loadData();
        //init info partner
        initPartnerInfo();
        //event click
        txtSend.setOnClickListener(this);
        emojiImageView.setOnClickListener(this);
        txtMicro.setOnClickListener(this);
        txtGallery.setOnClickListener(this);
        txtCamera.setOnClickListener(this);
        txtRecord.setOnClickListener(this);
        imgRecord.setOnClickListener(this);

        mRecycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//    private void hideKeyboard(View view) {
//        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        im.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//    }

    private void loadData() {
        try {
            Query query = presenter.getAllChat(getUid(), partnerID);
            mAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(ChatMessage.class, R.layout.row_chat,
                    ChatMessageViewHolder.class, query) {
                @Override
                protected void populateViewHolder(ChatMessageViewHolder viewHolder, ChatMessage model, int position) {
                    viewHolder.bindToView(model);
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    });
                    // mRecycler.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            };
            mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int count = mAdapter.getItemCount();
                    int lastPosition = mManager.findLastVisibleItemPosition();
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastPosition == -1 || (positionStart >= (count - 1) && lastPosition == (positionStart - 1))) {
                        mRecycler.scrollToPosition(positionStart);
                    }

                }
            });
            mRecycler.setLayoutManager(mManager);
            mRecycler.setAdapter(mAdapter);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        if (view == txtSend) {
            addNewMessage();
        } else if (view == emojiImageView) {
            openIcons();
        } else if (view == txtMicro) {
            promptSpeechInput();
        } else if (view == txtGallery) {
            openGallery();
        } else if (view == txtCamera) {
            openCamera();
        } else if (view == txtRecord) {
            imgRecord.setVisibility(View.VISIBLE);
        } else if (view == imgRecord) {

        }
    }

    private void recordAudio() {
        if (verifyLocationPermission()) {
            String timestamp = String.valueOf((new Date().getTime() / 1000));
            audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                    timestamp + ".3gp";
            mediaRecordReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void mediaRecordReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(audioSavePathInDevice);
    }

    private boolean verifyLocationPermission() {
        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, PERMISSION_GROUP, REQUEST_RECORD_PERMISSION);
            return false;
        }
        int audioPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (audioPermission != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, PERMISSION_GROUP, REQUEST_RECORD_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //displayLocation();
                }
                break;
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, Constants.CAMERA_INTENT);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, Constants.GALLERY_INTENT);
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, Constants.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                edtMessage.setText(result.get(0));
            }
        } else if (requestCode == Constants.GALLERY_INTENT) {
            if (resultCode == RESULT_OK) {
                try {
//                    Thread thread = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    //get time
                    final double start = new Date().getTime() / 1000;
                    String filePath = getRealPathFromURI(data.getData());
                    byte[] bitmapImage = encodeImage(filePath);
                    //upload to server then get path
                    String timestamp = String.valueOf(new Date().getTime() / 1000);
                    String fileName = String.format("%s%s", getUid(), timestamp);
                    StorageReference filePathImageChat = mStorage.child(Constants.CHAT_PHOTO).child(fileName);
                    filePathImageChat.putBytes(bitmapImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String prefix = "<img>";
                            String result = prefix.concat(taskSnapshot.getDownloadUrl().toString());
                            //add image
                            ChatMessage message = new ChatMessage(fromName, result, new Date().getTime() / 1000, toName);
                            presenter.addChatMessage(partnerID, message);

                            double end = new Date().getTime() / 1000;
                            Log.d(TAG, "" + (end - start));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("Có lõi, vui lòng thử lại");
                        }
                    });
                    // }
                    // });
                    //   thread.start();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else if (requestCode == Constants.CAMERA_INTENT) {
            if (resultCode == RESULT_OK) {

                try {
                    String filePath = getRealPathFromURI(data.getData());
                    byte[] bitmapImage = encodeImage(filePath);
                    //upload to server then get path
                    String timestamp = String.valueOf(new Date().getTime() / 1000);
                    String fileName = String.format("%s%s", getUid(), timestamp);
                    StorageReference filePathImageChat = mStorage.child(Constants.CHAT_PHOTO).child(fileName);
                    filePathImageChat.putBytes(bitmapImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String prefix = "<img>";
                            String result = prefix.concat(taskSnapshot.getDownloadUrl().toString());
                            //add image
                            ChatMessage message = new ChatMessage(fromName, result, new Date().getTime() / 1000, toName);
                            presenter.addChatMessage(partnerID, message);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("Có lõi, vui lòng thử lại");
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //hiển thị thông báo
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] arrByte = baos.toByteArray();
        String encodeImage = Base64.encodeToString(arrByte, Base64.DEFAULT);
        return encodeImage;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] encodeImage(String path) {
        // get correct scale
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;

        //using ExifInterface to set correct orientation
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        //encode image
        File imgFile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imgFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis, null, o2);
        //rotate orientation
        Bitmap bmRotated = rotateBitmap(bitmap, orientation);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmRotated.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] arrByte = baos.toByteArray();
        return arrByte;

    }

    private void openIcons() {
        setEmojiconFragment(false);
        frameIcon.setVisibility(View.VISIBLE);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

        EmojiconsFragment.input(edtMessage, emojicon);
    }

    /**
     * It called, when backspace button of Emoticons pressed
     *
     * @param view
     */
    @Override
    public void onEmojiconBackspaceClicked(View view) {

        EmojiconsFragment.backspace(edtMessage);
    }

    private void addNewMessage() {
        frameIcon.setVisibility(View.GONE);
        boolean result = true;
        if (TextUtils.isEmpty(edtMessage.getText().toString())) {
            result = false;
            edtMessage.setError(getResources().getString(R.string.batBuoc));
        }
        if (result) {
            //send push notification
            try {
                String[] regIds = {deviceToken};

                JSONArray regArray = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    regArray = new JSONArray(regIds);
                }
                PushMessage.sendMessage(regArray, fromName, edtMessage.getText().toString(), "", "Xin chào");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ChatMessage message = new ChatMessage(fromName, edtMessage.getText().toString(), new Date().getTime() / 1000, toName);
            presenter.addChatMessage(partnerID, message);
            //clear text and show keyboard
            edtMessage.setText(null);
        }
    }

    private void initPartnerInfo() {
        mUserPartnerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        txtPartnerName.setText(user.getDisplayName());
                        toName = user.getDisplayName();
                        //txtOld.setText(String.valueOf(user.getOld()));
                        txtAddress.setText(user.getAddress().get(Constants.ADDRESS).toString());
                        // ImageLoader.getInstance().loadImage(ChatActivity.this, user.getPhotoURL(), imgAvatarPartner);
                        deviceToken = user.getDeviceToken();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
        //current user
        mUserCurrentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        fromName = user.getDisplayName();
                        currentAvatar = user.getPhotoURL();
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
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof RecyclerView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    frameIcon.setVisibility(View.GONE);
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            }
        }
        return super.dispatchTouchEvent(event);
    }
}
