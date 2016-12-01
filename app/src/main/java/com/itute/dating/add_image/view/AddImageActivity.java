package com.itute.dating.add_image.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.itute.dating.R;
import com.itute.dating.add_image.ClickListener;
import com.itute.dating.add_image.GalleryAdapter;
import com.itute.dating.add_image.model.Image;
import com.itute.dating.add_image.model.Photo;
import com.itute.dating.add_image.presenter.AddImagePresenter;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.util.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 07/11/2016.
 */
public class AddImageActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_UID = "extra_uid";
    public static final String TAG = "AddImageActivity";
    private AddImagePresenter presenter;
    public ProgressDialog progressDialog;
    private ArrayList<Image> images;
    private RecyclerView mRecycler;
    private GalleryAdapter mAdapter;
    public LinearLayout photoContainer;
    private String intentUid;

    @BindView(R.id.txtAddImage)
    TextView txtAddImage;
    @BindView(R.id.txtPost)
    TextView txtPost;
    @BindView(R.id.btnAppBack)
    TextView txtBack;

    private ArrayList<Uri> _eventImageUris = new ArrayList<>();
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1001;
    private static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        ButterKnife.bind(this);

        mRecycler = (RecyclerView) findViewById(R.id.recyclerView_image);
        //get intent from profileuseractivity
        intentUid = getIntent().getStringExtra(EXTRA_UID);
        if (!intentUid.equals(getUid())) {
            txtAddImage.setVisibility(View.GONE);
            txtPost.setVisibility(View.GONE);
        } else {
            txtAddImage.setVisibility(View.VISIBLE);
        }

        photoContainer = (LinearLayout) findViewById(R.id.photoContainer);

        presenter = new AddImagePresenter(this);

        //event click
        txtAddImage.setOnClickListener(this);
        txtPost.setOnClickListener(this);
        txtBack.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        loadData();

    }

    @Override
    public void onClick(View view) {
        if (view == txtAddImage) {
            addPhotoWithPermission();
        } else if (view == txtPost) {
            postImage();
        } else if (view == txtBack) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private class AsyncTaskForLoadData extends AsyncTask<Void, Void, Void> {
        public AsyncTaskForLoadData() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //clear data
            mAdapter.clearAllImage();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(Constants.PHOTOS).child(intentUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mAdapter.clearAllImage();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Photo photo = snapshot.getValue(Photo.class);
                        if (photo != null) {
                            for (int i = 0; i < photo.getPhotoLink().size(); i++) {
                                Image image = new Image(photo.getPhotoLink().get(i));
                                images.add(image);
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void postImage() {
        //progress dialog
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        //end create progress dialog
        presenter.addUrlPhotoUploaded(BaseActivity.getUid(), _eventImageUris);
        //hide textview post
        txtPost.setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initViews();

    }

    private void loadData() {
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(images, this);

        //config recyeler view
        mRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        //cache recycler view
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemViewCacheSize(20);
        mRecycler.setDrawingCacheEnabled(true);
        mRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //set adapter
        mRecycler.setAdapter(mAdapter);

        AsyncTaskForLoadData asyncTaskForLoadData = new AsyncTaskForLoadData();
        asyncTaskForLoadData.execute();
        //set on item click
        mRecycler.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(this, mRecycler, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideShowDialogFragment newFragment = new SlideShowDialogFragment();

                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void initViews() {
        if (_eventImageUris.size() > 0) {
            txtPost.setVisibility(View.VISIBLE);
        } else {
            txtPost.setVisibility(View.GONE);
        }
    }


    private boolean verifyStoragePermission() {
        int readPermission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_CODE_READ_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    private void addPhotoWithPermission() {
        if (verifyStoragePermission()) {
            addPhoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPhoto();
                }
                break;
        }
    }

    private void addPhoto() {
        Config config = new Config();
        config.setSelectionMin(1);
        config.setSelectionLimit(10);

        ImagePickerActivity.setConfig(config);

        Intent myIntent = new Intent(AddImageActivity.this, ImagePickerActivity.class);
        myIntent.putParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, _eventImageUris);
        startActivityForResult(myIntent, Constants.INTENT_REQUEST_GET_IMAGES);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_REQUEST_GET_IMAGES && resultCode == RESULT_OK) {
            ArrayList<Uri> image_uris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);
            _eventImageUris.clear();
            for (Uri uri : image_uris) {
                _eventImageUris.add(uri);
            }
            onPickImageSuccess();
        }
    }

    private void onPickImageSuccess() {
        int previewImageSize = 500;
        photoContainer.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(previewImageSize, previewImageSize);
        params.setMargins(5, 0, 5, 0);

        for (Uri uri : _eventImageUris) {
            ImageView photo = new ImageView(this);
            photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            photo.setLayoutParams(params);
            //using Glide to load image
            Glide.with(AddImageActivity.this)
                    .load(uri.toString())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(photo);
            photoContainer.addView(photo);
        }
    }
}
