package com.itute.dating.add_friend.model;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.profile_user.model.User;

/**
 * Created by buivu on 02/11/2016.
 */
public class AddFriendViewHolder extends RecyclerView.ViewHolder {
    public ImageView imgAvatar;
    public TextView txtName, txtSuccess, txtFail;
    public Button btnDongY, btnHuyBo;

    public AddFriendViewHolder(View itemView) {
        super(itemView);

        imgAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar_add_friend);
        txtName = (TextView) itemView.findViewById(R.id.txtNameAddFriend);
        btnDongY = (Button) itemView.findViewById(R.id.btn_xacNhan);
        btnHuyBo = (Button) itemView.findViewById(R.id.btn_xoa);
        txtSuccess = (TextView) itemView.findViewById(R.id.txt_success);
        txtFail = (TextView) itemView.findViewById(R.id.txt_fail);
    }

    public void bindToListHeart(Activity activity, User user) {
        ImageLoader.getInstance().loadImage(activity, user.getPhotoURL(), imgAvatar);
        txtName.setText(user.getDisplayName());
    }
}
