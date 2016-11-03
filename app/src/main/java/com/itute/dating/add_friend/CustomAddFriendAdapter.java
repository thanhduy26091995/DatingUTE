package com.itute.dating.add_friend;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.add_friend.model.AddFriendViewHolder;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.profile_user.view.ProfileUserActivity;
import com.itute.dating.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by buivu on 02/11/2016.
 */
public class CustomAddFriendAdapter extends RecyclerView.Adapter<AddFriendViewHolder> {

    // private List<User> listUser;
    private List<String> listUserId;
    private Activity mActivity;
    private DatabaseReference mDatabase;

    public CustomAddFriendAdapter(Activity mActivity, List<String> listUserId) {
        this.mActivity = mActivity;
        this.listUserId = listUserId;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onBindViewHolder(final AddFriendViewHolder holder, final int position) {
        //lấy userID theo đúng position
        final String strUserId = listUserId.get(position);
        Log.d("adapter", strUserId);
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
        //event click item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(mActivity, ProfileUserActivity.class);
                myIntent.putExtra(ProfileUserActivity.EXTRA_UID, listUserId.get(position));
                mActivity.startActivity(myIntent);
            }
        });
        //event click xacNhan
        holder.btnDongY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //thêm 1 node vào friends
                Map<String, Object> friendsMine = new HashMap<String, Object>();
                friendsMine.put(listUserId.get(position), true);

                Map<String, Object> friendsPartner = new HashMap<String, Object>();
                friendsPartner.put(BaseActivity.getUid(), true);
                //cập nhật bạn bè 2 chiều
                mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.FRIENDS).updateChildren(friendsMine);
                mDatabase.child(Constants.USERS).child(listUserId.get(position)).child(Constants.FRIENDS).updateChildren(friendsPartner);
                //xóa node này trong requets
                mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.REQUESTS).child(listUserId.get(position)).removeValue();
                //disable 2 nút xác nhận và đồng ý
                holder.btnDongY.setVisibility(View.GONE);
                holder.btnHuyBo.setVisibility(View.GONE);
                //cập nhật só lượng bạn bè
                mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                int numFriend = user.getFriendCount();
                                Map<String, Object> updateFriendCount = new HashMap<String, Object>();
                                updateFriendCount.put(Constants.FRIEND_COUNT, numFriend + 1);
                                mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).updateChildren(updateFriendCount);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                //cập nhật só lượng bạn bè
                mDatabase.child(Constants.USERS).child(listUserId.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                int numFriend = user.getFriendCount();
                                Map<String, Object> updateFriendCount = new HashMap<String, Object>();
                                updateFriendCount.put(Constants.FRIEND_COUNT, numFriend + 1);
                                mDatabase.child(Constants.USERS).child(listUserId.get(position)).updateChildren(updateFriendCount);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    //using for pull to refresh
    public void clear() {
        listUserId.clear();
        notifyDataSetChanged();
    }


    @Override
    public AddFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mActivity, R.layout.item_add_friend, null);
        return new AddFriendViewHolder(rootView);
    }

    @Override
    public int getItemCount() {
        return listUserId.size();
    }
}
