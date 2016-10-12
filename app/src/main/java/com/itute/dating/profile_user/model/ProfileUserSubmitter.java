package com.itute.dating.profile_user.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itute.dating.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 11/10/2016.
 */
public class ProfileUserSubmitter {
    private DatabaseReference mDatabase;
    private byte[] bitmapDataUser = null;
    private StorageReference mStorage;

    public ProfileUserSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    //get user theo uid
    public DatabaseReference getUser(String uid) {
        return mDatabase.child(Constants.USERS).child(uid);
    }

    //chỉnh sửa thông tin User
    public void editUser(User user, String uid) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.DISPLAY_NAME, user.getDisplayName());
        myMap.put(Constants.GENDER, user.getGender());
        myMap.put(Constants.PHONE, user.getPhone());
        myMap.put(Constants.DATE_OF_BIRTH, user.getDateOfBirth());
        myMap.put(Constants.STAR, user.getStar());
        myMap.put(Constants.ADDRESS, user.getAddress());
        myMap.put(Constants.JOB, user.getJob());
        myMap.put(Constants.LANGUAGE, user.getLanguage());
        myMap.put(Constants.RELIGION, user.getReligion());
        myMap.put(Constants.HOBBY, user.getHobby());
        myMap.put(Constants.OLD, user.getOld());
        mDatabase.child(Constants.USERS).child(uid).updateChildren(myMap);
    }

    //chỉnh sửa user photoURL
    public void editUserPhotoURL(String uid, String photoURL) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.PHOTO_URL, photoURL);
        mDatabase.child(Constants.USERS).child(uid).updateChildren(myMap);
    }

    public void addImageUser(String uid, String avatarName, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        if (Constants.USER_FILE_PATH != null) {
            bitmapDataUser = resizeImage(Constants.USER_FILE_PATH);
        }
        if (bitmapDataUser != null) {
            StorageReference filePathAvatar = mStorage.child(Constants.USER_AVATAR).child(uid).child(avatarName);
            UploadTask uploadTask = filePathAvatar.putBytes(bitmapDataUser);
            uploadTask.addOnSuccessListener(listener);

            //restart bitmap
            Constants.USER_FILE_PATH = null;
        }
    }

    //resize ảnh trước khi show lên ImageView, để giảm dung lượng ảnh
    public byte[] resizeImage(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.outWidth = 50; //pixels
        options.outHeight = 50; //pixels
        options.inSampleSize = 2;
        File file = new File(filePath);
        FileInputStream in = null; // here, you need to get your context.
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] bitmapdata = baos.toByteArray();
        return bitmapdata;
    }
}
