package com.itute.dating.chat_group.model;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.profile_user.model.User;

/**
 * Created by buivu on 05/12/2016.
 */
public class ChatGroupViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName;
    public ImageView imgAvatar;
    public CheckBox chkCheck;

    public ChatGroupViewHolder(View itemView) {
        super(itemView);
        //init
        txtName = (TextView) itemView.findViewById(R.id.txt_username_chat_group);
        imgAvatar = (ImageView) itemView.findViewById(R.id.img_avatar_chat_group);
        chkCheck = (CheckBox) itemView.findViewById(R.id.chk_chat_group);
    }

    public void bindData(Activity activity, User user) {
        txtName.setText(user.getDisplayName());
        ImageLoader.getInstance().loadImage(activity, user.getPhotoURL(), imgAvatar);
    }
}
