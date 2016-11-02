package com.itute.dating.user_list.model;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;

import org.w3c.dom.Text;

/**
 * Created by buivu on 08/10/2016.
 */
public class UserListViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName, txtStatus, txtAddress, txtState;
    public LinearLayout linearHeart, linearChat, linearAddFriend;
    public TextView txtIconHeart, txtHeart, txtIconAddFriend, txtAddFriend;
    public ImageView imgAvatar;

    public UserListViewHolder(View itemView) {
        super(itemView);
        //init
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
        txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
        txtState = (TextView) itemView.findViewById(R.id.txtState);
        imgAvatar = (ImageView) itemView.findViewById(R.id.avatar);
        linearHeart = (LinearLayout) itemView.findViewById(R.id.linearHeart);
        linearChat = (LinearLayout) itemView.findViewById(R.id.linearChat);
        linearAddFriend = (LinearLayout) itemView.findViewById(R.id.linearAddFriend);
        txtIconHeart = (TextView) itemView.findViewById(R.id.iconHeart);
        txtHeart = (TextView) itemView.findViewById(R.id.txtHeart);
        txtIconAddFriend = (TextView) itemView.findViewById(R.id.iconAddFriend);
        txtAddFriend = (TextView) itemView.findViewById(R.id.txtAddFriend);
    }

    public void bindToViewHolder(User user) {
        txtName.setText(String.format("%s (%d tuổi)", user.getDisplayName(), user.getOld()));
        txtAddress.setText(user.getAddress().get(Constants.ADDRESS).toString());
        if (user.getIsLogin()) {
            txtState.setText("Online");
        } else {
            txtState.setText("Offline");
        }
        txtStatus.setText(user.getStatus());
    }
}
