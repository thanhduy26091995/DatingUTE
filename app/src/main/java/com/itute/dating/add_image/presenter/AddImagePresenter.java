package com.itute.dating.add_image.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itute.dating.add_image.AsyncTaskForAddImage;
import com.itute.dating.add_image.UploadPhotoThreadListener;
import com.itute.dating.add_image.view.AddImageActivity;
import com.itute.dating.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 07/11/2016.
 */
public class AddImagePresenter {
    private AddImageActivity view;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    public AddImagePresenter(AddImageActivity view) {
        this.view = view;
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public ArrayList<String> uploadImage(String uid, ArrayList<Uri> arrayList) {
        Log.d("URL", "onUploadImage");
        final ArrayList<String> uploaded = new ArrayList<>();
        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .build();
        // Upload files
        String timeStamp = String.valueOf(new Date().getTime());
        mStorage = mStorage.child(Constants.USER_PHOTOS).child(uid);

        for (int i = 0; i < arrayList.size(); i++) {
            Uri uri = arrayList.get(i);
            byte[] arrBytes = resizeImage(uri.getPath());
            //upload
            UploadTask uploadTask = mStorage.child(timeStamp).putBytes(arrBytes, metadata);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    uploaded.add(taskSnapshot.getDownloadUrl().toString());
                    Log.d("URL", taskSnapshot.getDownloadUrl().toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(view.TAG, e.getMessage());
                }
            });
            arrBytes = null;
        }
        return uploaded;

    }

    public void addUrlPhotoUploaded(final String uid, ArrayList<Uri> arrayListPhotoUri) {
        //get timestamp
        final String timestamp = String.valueOf(new Date().getTime());
        final Map<String, Object> myMap = new HashMap<>();


        UploadPhotoThreadListener uploadPhotoThreadListener = new UploadPhotoThreadListener() {
            @Override
            public void onUploadPhotoSuccess(ArrayList<String> photoUrls) {
                Log.d(view.TAG, "onThreadListener");
                myMap.put(Constants.PHOTO_LINK, photoUrls);
                mDatabase.child(Constants.PHOTOS).child(uid).child(timestamp).setValue(myMap);
                //dismiss progress dialog
                if (view.progressDialog.isShowing()) {
                    view.progressDialog.dismiss();
                }
                view.photoContainer.removeAllViews();
            }
        };
        new AsyncTaskForAddImage(view, uid, uploadPhotoThreadListener, arrayListPhotoUri).execute();
    }

    //resize imgage before upload
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
