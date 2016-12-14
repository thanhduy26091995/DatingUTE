package com.itute.dating.chat_group.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.chat_group.model.ChatGroupInfo;
import com.itute.dating.chat_group.model.ChatGroupMessage;
import com.itute.dating.chat_group.model.ChatGroupViewHolder;
import com.itute.dating.chat_group.presenter.ChatGroupPresenter;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;
import com.itute.dating.util.MyLinearLayoutManager;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 08/12/2016.
 */
public class ChatGroupActivity extends BaseActivity implements View.OnClickListener {

    public static final String GROUP_CHAT_ID = "groupChatId";
    private ChatGroupPresenter presenter;
    private String groupId = "";
    private static final String TAG = "ChatGroupActivity";
    private FirebaseRecyclerAdapter<ChatGroupMessage, ChatGroupViewHolder> mAdapter;
    private MyLinearLayoutManager mManager;
    private DatabaseReference mDatabase;
    private String avatarSender;

    @BindView(R.id.et_message)
    EditText edtMessage;
    @BindView(R.id.btn_confirm)
    TextView txtSend;
    @BindView(R.id.rc_chat)
    RecyclerView mRecycler;
    @BindView(R.id.txt_setting)
    TextView txtSetting;
    @BindView(R.id.txt_group_name)
    TextView txtGroupName;
    @BindView(R.id.iv_avatar)
    ImageView imgGroupAvatar;

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 99;
    private static final String[] PERMISSION_GROUP = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private ImageView imgGroupName;
    private String groupName, groupAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        ButterKnife.bind(this);
        //get intent
        groupId = getIntent().getStringExtra(GROUP_CHAT_ID);
        //init
        mDatabase = FirebaseDatabase.getInstance().getReference();
        presenter = new ChatGroupPresenter(this);
        mManager = new MyLinearLayoutManager(this);
        //cache recycler view
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemViewCacheSize(1000);
        mRecycler.setDrawingCacheEnabled(true);
        mRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //event click
        txtSend.setOnClickListener(this);
        txtSetting.setOnClickListener(this);
        Log.d(TAG, "onCreate");
        //load data

        AsyncTaskForLoadDataChat asyncTaskForLoadDataChat = new AsyncTaskForLoadDataChat();
        asyncTaskForLoadDataChat.execute();


    }

    @Override
    public void onClick(View view) {
        if (view == txtSend) {
            addNewMessageToGroup();
        } else if (view == txtSetting) {
            showAlertSetting();
        }
    }

    private boolean verifyLocationPermission() {
        int locationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            //request permission
            ActivityCompat.requestPermissions(this, PERMISSION_GROUP, REQUEST_READ_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
        }
    }

    private void showAlertSetting() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.custom_alertdialog_setting_chat_group, null);
        builder.setView(v);
        //get instance of view in custom-alert-dialog
        final EditText edtGroupName = (EditText) v.findViewById(R.id.edt_group_name);
        imgGroupName = (ImageView) v.findViewById(R.id.img_avatar_group);
        //hiển thị data
        edtGroupName.setText(groupName);
        ImageLoader.getInstance().loadImage(ChatGroupActivity.this, groupAvatar, imgGroupName);
        //event click choose image
        imgGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Intent.ACTION_PICK);
                myIntent.setType("image/*");
                startActivityForResult(myIntent, Constants.GALLERY_INTENT);
            }
        });
        //create button Ok
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                txtGroupName.setText(edtGroupName.getText());
                presenter.addChatGroupInfo(groupId, edtGroupName.getText().toString());
            }
        });
        //create button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.create().dismiss();
            }
        });
        //show dialog
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.GALLERY_INTENT && resultCode == RESULT_OK) {
            ImageLoader.getInstance().loadImage(ChatGroupActivity.this, data.getData().toString(), imgGroupName);
            Constants.AVATAR_GROUP_CHAT = getRealPathFromURI(data.getData());
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void initInfo() {
        //load group info
        //show data len6chat-info
        mDatabase.child(Constants.CHAT_GROUP).child(groupId).child(Constants.GROUP_INFO).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    ChatGroupInfo chatGroupInfo = dataSnapshot.getValue(ChatGroupInfo.class);
                    if (chatGroupInfo != null) {
                        groupName = chatGroupInfo.getGroupName();
                        groupAvatar = chatGroupInfo.getGroupAvatar();
                        txtGroupName.setText(chatGroupInfo.getGroupName());
                        ImageLoader.getInstance().loadImage(ChatGroupActivity.this, chatGroupInfo.getGroupAvatar(), imgGroupAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //load avatar sender
        mDatabase.child(Constants.USERS).child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        avatarSender = user.getPhotoURL();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addNewMessageToGroup() {
        String timestamp = String.valueOf(new Date().getTime());
        String content = edtMessage.getText().toString();
        String sendBy = getUid();
        ChatGroupMessage chatGroupMessage = new ChatGroupMessage(content, sendBy, avatarSender, timestamp);
        presenter.addChatGroupMessage(groupId, chatGroupMessage);
        //clear text and show keyboard
        edtMessage.setText(null);
    }

    private class AsyncTaskForLoadDataChat extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showProgessDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            initInfo();
            loadData();
            //hideProgressDialog();
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

    private void loadData() {
        Query query = presenter.getAllChatGroup(groupId);
        mAdapter = new FirebaseRecyclerAdapter<ChatGroupMessage, ChatGroupViewHolder>(ChatGroupMessage.class, R.layout.row_chat_group,
                ChatGroupViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final ChatGroupViewHolder viewHolder, final ChatGroupMessage model, final int position) {

                //gán data cho view
                viewHolder.bindToView(getUid(), model);
                //set avatar
                // if (position == 0) {
                viewHolder.chatView.setAvatar(model.getAvatarSender());
//                } else if (position > 0) {
//                    ChatGroupMessage message = mAdapter.getItem(position - 1);
//                    if (message.getSendBy().equals(model.getSendBy())) {
//                        viewHolder.chatView.removeAvatar();
//                    } else {
//                        viewHolder.chatView.setAvatar(model.getAvatarSender());
//                    }
//                }
                //load avatar ảnh đại diện
//                mDatabase.child(Constants.USERS).child(model.getSendBy()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot != null) {
//                            User user = dataSnapshot.getValue(User.class);
//                            if (user != null) {
//                                if (position == 0) {
//                                    viewHolder.chatView.setAvatar(user.getPhotoURL());
//                                } else if (position > 0) {
//                                    ChatGroupMessage message = mAdapter.getItem(position - 1);
//                                    if (message.getSendBy().equals(model.getSendBy())) {
//                                        viewHolder.chatView.removeAvatar();
//                                    } else {
//                                        viewHolder.chatView.setAvatar(user.getPhotoURL());
//                                    }
//                                }
//                                Glide.get(ChatGroupActivity.this).clearMemory();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
                //ẩn bàn phím
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                });
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
    }
}
