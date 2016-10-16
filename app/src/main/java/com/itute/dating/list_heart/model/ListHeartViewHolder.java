package com.itute.dating.list_heart.model;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.profile_user.model.User;

/**
 * Created by buivu on 16/10/2016.
 */
public class ListHeartViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgAvatar;
    public TextView txtName;

    public ListHeartViewHolder(View itemView) {
        super(itemView);
        imgAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        txtName = (TextView) itemView.findViewById(R.id.txtNameHeart);
    }

    public void bindToListHeart(Activity activity, User user) {
        ImageLoader.getInstance().loadImage(activity, user.getPhotoURL(), imgAvatar);
        txtName.setText(user.getDisplayName());
    }
}
