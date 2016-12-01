package com.itute.dating.add_image;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itute.dating.add_image.presenter.AddImagePresenter;
import com.itute.dating.add_image.view.AddImageActivity;
import com.itute.dating.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by buivu on 07/11/2016.
 */
public class AsyncTaskForAddImage extends AsyncTask<String, String, ArrayList<String>> {

    private ArrayList<Uri> arrayListUri;
    private String uid;
    private ProgressDialog progressDialog;
    private AddImageActivity view;
    private AddImagePresenter presenter;
    private ArrayList<String> arrayData;
    private UploadPhotoThreadListener listener;

    public AsyncTaskForAddImage() {
    }

    public AsyncTaskForAddImage(AddImageActivity view, String uid, UploadPhotoThreadListener listener, ArrayList<Uri> arrayListUri) {
        this.uid = uid;
        this.view = view;
        this.listener = listener;
        this.arrayListUri = arrayListUri;
        presenter = new AddImagePresenter(view);
        arrayData = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        try {
            if (arrayListUri.size() != 0) {
                return uploadImage(uid, arrayListUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(ArrayList<String> arrayList) {
        // super.onPostExecute(arrayList);

        listener.onUploadPhotoSuccess(arrayList);
    }

    public ArrayList<String> uploadImage(String uid, ArrayList<Uri> arrayList) {

        try {
            StorageReference mStorage = FirebaseStorage.getInstance().getReference();
            Log.d("URL", "onUploadImage");
            final ArrayList<String> uploaded = new ArrayList<>();
            // Create the file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();
            // Upload files
            String timeStamp = String.valueOf(new Date().getTime());

            mStorage = mStorage.child(Constants.USER_PHOTOS).child(uid);
            final CountDownLatch countDownLatch = new CountDownLatch(arrayList.size());
            for (int i = 0; i < arrayList.size(); i++) {
                String fileName = timeStamp + "_" + i;
                Uri uri = arrayList.get(i);
                byte[] arrBytes = resizeImage(uri.getPath());
                //upload
                UploadTask uploadTask = mStorage.child(fileName).putBytes(arrBytes, metadata);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        countDownLatch.countDown();
                        uploaded.add(taskSnapshot.getDownloadUrl().toString());
                        Log.d(view.TAG, taskSnapshot.getDownloadUrl().toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(view.TAG, e.getMessage());
                        countDownLatch.countDown();
                    }
                });
                arrBytes = null;
            }
            //Wait until upload images done!
            countDownLatch.await();
            return uploaded;
        } catch (InterruptedException e) {
            Log.e(view.TAG, e.getMessage());
        }
        return null;
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
