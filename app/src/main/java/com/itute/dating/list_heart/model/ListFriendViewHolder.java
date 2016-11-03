package com.itute.dating.list_heart.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itute.dating.R;

/**
 * Created by buivu on 03/11/2016.
 */
public class ListFriendViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgAvatar;
    public TextView txtName, txtChat, txtUnfriend;

    public ListFriendViewHolder(View itemView) {
        super(itemView);
        imgAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar_profile_list_friend);
        txtName = (TextView) itemView.findViewById(R.id.txtNameFriend);
        txtChat = (TextView) itemView.findViewById(R.id.txt_chat);
        txtUnfriend = (TextView) itemView.findViewById(R.id.txt_unfriend);

    }
}
