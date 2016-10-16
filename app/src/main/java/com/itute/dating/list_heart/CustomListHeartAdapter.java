package com.itute.dating.list_heart;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.list_heart.model.ListHeartViewHolder;
import com.itute.dating.profile_user.model.User;

import java.util.List;

/**
 * Created by buivu on 17/10/2016.
 */
public class CustomListHeartAdapter extends RecyclerView.Adapter<ListHeartViewHolder> {
    private List<User> listUser;
    private Activity mContext;

    public CustomListHeartAdapter(Activity mContext, List<User> listUser) {
        this.mContext = mContext;
        this.listUser = listUser;
    }

    @Override
    public ListHeartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_heart, null);
        return new ListHeartViewHolder(rootView);
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    @Override
    public void onBindViewHolder(ListHeartViewHolder holder, int position) {
        //lấy userID theo đúng position
        final User userCurrent = listUser.get(position);
        //load data
        ImageLoader.getInstance().loadImage(mContext, userCurrent.getPhotoURL(), holder.imgAvatar);
        holder.txtName.setText(userCurrent.getDisplayName());
    }
}
