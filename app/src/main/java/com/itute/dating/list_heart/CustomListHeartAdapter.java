package com.itute.dating.list_heart;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.list_heart.model.ListHeartViewHolder;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.profile_user.view.ProfileUserActivity;
import com.itute.dating.util.Constants;

import java.util.List;

/**
 * Created by buivu on 17/10/2016.
 */
public class CustomListHeartAdapter extends RecyclerView.Adapter<ListHeartViewHolder> {
    private List<String> listUserId;
    private Activity mContext;
    private DatabaseReference mDatabase;
    private static final String TAG = "CustomListHeartAdapter";

    public CustomListHeartAdapter(Activity mContext, List<String> listUserId) {
        this.mContext = mContext;
        this.listUserId = listUserId;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ListHeartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_heart, null);
        return new ListHeartViewHolder(rootView);
    }

    @Override
    public int getItemCount() {
        return listUserId.size();
    }

    //using for pull to refresh
    public void clear() {
        listUserId.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ListHeartViewHolder holder, final int position) {
        //lấy userID theo đúng position
        final String strUserId = listUserId.get(position);
        mDatabase.child(Constants.USERS).child(strUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        holder.txtName.setText(user.getDisplayName());
                        ImageLoader.getInstance().loadImage(mContext, user.getPhotoURL(), holder.imgAvatar);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //event click cho item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String partnerId = listUserId.get(position);
                Intent myIntent = new Intent(mContext, ProfileUserActivity.class);
                myIntent.putExtra(ProfileUserActivity.EXTRA_UID, partnerId);
                mContext.startActivity(myIntent);
                mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
