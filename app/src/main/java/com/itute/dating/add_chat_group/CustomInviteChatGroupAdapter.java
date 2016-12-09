package com.itute.dating.add_chat_group;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.add_chat_group.model.ChatGroupViewHolder;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by buivu on 05/12/2016.
 */
public class CustomInviteChatGroupAdapter extends RecyclerView.Adapter<ChatGroupViewHolder> {

    private Activity mActivity;
    private List<String> listUserId;
    private List<String> listMember;
    private DatabaseReference mDatabase;
    private static final String TAG = "CustomInviteChatGroupAdapter";
    private LinearLayout photoContainer;
    private static Boolean isTouched = false;

    public CustomInviteChatGroupAdapter(Activity mActivity, List<String> listUserId, List<String> listMember) {
        this.mActivity = mActivity;
        this.listUserId = listUserId;
        this.listMember = listMember;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        photoContainer = (LinearLayout) mActivity.findViewById(R.id.photoContainer);
    }


    @Override
    public ChatGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mActivity, R.layout.item_add_chat_group, null);
        return new ChatGroupViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ChatGroupViewHolder holder, final int position) {
        final String strUid = listUserId.get(position);

        mDatabase.child(Constants.USERS).child(strUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    final User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        holder.txtName.setText(user.getDisplayName());
                        ImageLoader.getInstance().loadImage(mActivity, user.getPhotoURL(), holder.imgAvatar);
                        int previewImageSize = 250;
                        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(previewImageSize, previewImageSize);
                        params.setMargins(5, 5, 5, 5);
                        //event click checkbox
                        holder.chkCheck.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                isTouched = true;
                                return false;
                            }
                        });
                        holder.chkCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                                if (isTouched) {
                                    isTouched = false;
                                    if (!isChecked) {
                                        photoContainer.removeAllViews();
                                    } else {
                                        CircleImageView photo = new CircleImageView(mActivity);
                                        photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        photo.setLayoutParams(params);
                                        //using Glide to load image
                                        Glide.with(mActivity)
                                                .load(user.getPhotoURL())
                                                .centerCrop()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .centerCrop()
                                                .into(photo);
                                        photoContainer.addView(photo);
                                        listMember.add(strUid);
                                    }
                                }

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listUserId.size();
    }
}
