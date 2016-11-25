package com.itute.dating.chat_list.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.chat.model.ChatMessage;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;
import com.itute.dating.util.PrettyTime;

import java.util.Date;

/**
 * Created by buivu on 14/10/2016.
 */
public class ChatListViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivAvatar;
    public TextView txtUsername, txtTimestamp, txtLastMessage;

    public ChatListViewHolder(View itemView) {
        super(itemView);

        ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        txtUsername = (TextView) itemView.findViewById(R.id.txtReceiverName);
        txtTimestamp = (TextView) itemView.findViewById(R.id.txtTimestamp);
        txtLastMessage = (TextView) itemView.findViewById(R.id.txt_last_message);
    }

    public void bindToChatList(final Context mContext, DatabaseReference mDatabase, String partnerID) {
        mDatabase.child(Constants.USERS).child(partnerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        //load ảnh sử dụng thư viện Glide
                        Glide.with(mContext)
                                .load(user.getPhotoURL())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(R.drawable.avatar)
                                .centerCrop()
                                .into(ivAvatar);
                        txtUsername.setText(user.getDisplayName());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child(Constants.MESSAGES).child(BaseActivity.getUid()).child(partnerID).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        ChatMessage message = data.getValue(ChatMessage.class);
                        if (message != null) {
                            txtTimestamp.setText(PrettyTime.format(new Date(), new Date(message.getTimestamp() * 1000)));

                            if (message.getMessage().startsWith("<img>")) {
                                if (message.getIsMine()) {
                                    txtLastMessage.setText("Bạn vừa gửi một ảnh");
                                } else {
                                    txtLastMessage.setText(String.format("%s vừa gửi một ảnh", message.getFromName()));
                                }
                            } else {
                                txtLastMessage.setText(message.getMessage());
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
