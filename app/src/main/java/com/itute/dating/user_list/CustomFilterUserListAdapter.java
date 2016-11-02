package com.itute.dating.user_list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itute.dating.R;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.user_list.model.UserListViewHolder;
import com.itute.dating.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 20/10/2016.
 */
public class CustomFilterUserListAdapter extends RecyclerView.Adapter<UserListViewHolder> {

    private List<User> list = new ArrayList<>();
    private Context mContext;

    public CustomFilterUserListAdapter(List<User> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_list_friend, parent, false);
        return new UserListViewHolder(itemView);
    }

    public void addItem(int position, User user) {
        list.add(position, user);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(UserListViewHolder holder, int position) {
        User user = list.get(position);
        holder.txtName.setText(user.getDisplayName());
        holder.txtAddress.setText(user.getAddress().get(Constants.ADDRESS).toString());
        if (user.getIsLogin()) {
            holder.txtState.setText("Online");
        } else {
            holder.txtState.setText("Offline");
        }
        holder.txtStatus.setText(user.getStatus());
    }


}
