package com.itute.dating.user_list.model;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.itute.dating.R;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;

import org.w3c.dom.Text;

/**
 * Created by buivu on 08/10/2016.
 */
public class UserListViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName, txtStatus, txtAddress, txtState;
    public ImageView imgAvatar;

    public UserListViewHolder(View itemView) {
        super(itemView);
        //init
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
        txtAddress = (TextView) itemView.findViewById(R.id.txtAddress);
        txtState = (TextView) itemView.findViewById(R.id.txtState);
        imgAvatar = (ImageView) itemView.findViewById(R.id.avatar);
    }

    public void bindToViewHolder(User user) {
        txtName.setText(user.getDisplayName());
        txtAddress.setText(user.getAddress().get(Constants.ADDRESS).toString());
        if (user.getIsLogin()) {
            txtState.setText("Online");
        } else {
            txtState.setText("Offline");
        }
    }
}
