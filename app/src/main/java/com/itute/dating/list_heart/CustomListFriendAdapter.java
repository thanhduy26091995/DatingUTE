package com.itute.dating.list_heart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.chat.view.ChatActivity;
import com.itute.dating.list_heart.model.ListFriendViewHolder;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.profile_user.view.ProfileUserActivity;
import com.itute.dating.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by buivu on 03/11/2016.
 */
public class CustomListFriendAdapter extends RecyclerView.Adapter<ListFriendViewHolder> {

    private Activity mActivity;
    private List<String> listUserId;
    private DatabaseReference mDatabase;
    private static final String TAG = "CustomListFriendAdapter";

    public CustomListFriendAdapter(Activity mActivity, List<String> listUserId) {
        this.mActivity = mActivity;
        this.listUserId = listUserId;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onBindViewHolder(final ListFriendViewHolder holder, final int position) {
        final String strUserId = listUserId.get(position);

        mDatabase.child(Constants.USERS).child(strUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        holder.txtName.setText(user.getDisplayName());
                        ImageLoader.getInstance().loadImage(mActivity, user.getPhotoURL(), holder.imgAvatar);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //event click for textview unfriend
        holder.txtUnfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show alert xác nhận
                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(R.string.confirmUnfriend)
                        .setCancelable(false)
                        .setPositiveButton(R.string.xacNhan, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                unFriend(position);
                            }
                        })
                        .setNegativeButton(R.string.huy, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                builder.create().show();

            }
        });
        //event click for textview chat
        holder.txtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String partnerId = listUserId.get(position);
                Intent myIntent = new Intent(mActivity, ChatActivity.class);
                myIntent.putExtra(ChatActivity.PARTNER_ID, partnerId);
                mActivity.startActivity(myIntent);
            }
        });
        //event click for itemview
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String partnerId = listUserId.get(position);
                Intent myIntent = new Intent(mActivity, ProfileUserActivity.class);
                myIntent.putExtra(ProfileUserActivity.EXTRA_UID, partnerId);
                mActivity.startActivity(myIntent);
            }
        });
    }

    private void unFriend(final int position) {
       /*
       cập nhật số lương trc, bởi vì khi node bị xóa, thì giá trị trong listUser sẽ bị thay đổi
       vậy nên cập nhật số lượng bạn bè trc sẽ không gặp tình trạng này
        */
        //cập nhật lại số lượng bạn bè
        mDatabase.child(Constants.USERS).child(listUserId.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        int numFriend = user.getFriendCount();
                        //cập nhật số lượng bạn bè
                        Map<String, Object> updateNumFriend = new HashMap<String, Object>();
                        updateNumFriend.put(Constants.FRIEND_COUNT, (numFriend - 1));
                        mDatabase.child(Constants.USERS).child(listUserId.get(position)).updateChildren(updateNumFriend);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //cập nhật lại số lượng bạn bè
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        int numFriend = user.getFriendCount();
                        //cập nhật số lượng bạn bè
                        Map<String, Object> updateNumFriend = new HashMap<String, Object>();
                        updateNumFriend.put(Constants.FRIEND_COUNT, (numFriend - 1));
                        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).updateChildren(updateNumFriend);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //xóa node này trong friends của cả 2 bên
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FRIENDS).child(listUserId.get(position)).removeValue();
        mDatabase.child(Constants.USERS).child(listUserId.get(position)).child(Constants.FRIENDS).child(BaseActivity.getUid()).removeValue();
    }

    @Override
    public ListFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mActivity, R.layout.item_profile_list_friend, null);
        return new ListFriendViewHolder(rootView);
    }

    @Override
    public int getItemCount() {
        return listUserId.size();
    }
}
