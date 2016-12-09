package com.itute.dating.chat_group.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itute.dating.R;
import com.itute.dating.base.view.IconTextView;

/**
 * Created by buivu on 08/12/2016.
 */
public class ChatView extends FrameLayout {

    private boolean isMine = true;
    private TextView tvMessage;
    private View vLeft;
    private View vRight;
    private View llMessage;
    private IconTextView ivLeft;
    private IconTextView ivRight;
    private ImageView ivLocation;
    private ImageView ivMyAvatar, ivPartnerAvatar;

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_IMAGE = 1;
    private DatabaseReference mDatabase;

    private TextView txtTitleRight, txtTitleLeft;


    public ChatView(Context context) {
        super(context);
        init();
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        LayoutInflater.from(getContext()).inflate(R.layout.item_view_chat_group, this, true);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        vLeft = findViewById(R.id.v_left);
        vRight = findViewById(R.id.v_right);
        ivLeft = (IconTextView) findViewById(R.id.iv_left_arrow);
        ivRight = (IconTextView) findViewById(R.id.iv_right_arrow);
        ivLocation = (ImageView) findViewById(R.id.iv_location);
        llMessage = findViewById(R.id.ll_message);
       ivMyAvatar = (ImageView) findViewById(R.id.avatar_chat);
        ivPartnerAvatar = (ImageView) findViewById(R.id.avatar_chat_partner);
       // txtTitleRight = (TextView) findViewById(R.id.title_right);
       // txtTitleLeft = (TextView) findViewById(R.id.title_left);

        update();
    }

    private void update() {
        if (isMine) {
            tvMessage.setBackgroundResource(R.drawable.border_color_primary);
            llMessage.setBackgroundResource(R.drawable.border_color_primary);
            tvMessage.setTextColor(Color.WHITE);
            vLeft.setVisibility(VISIBLE);
            vRight.setVisibility(GONE);
            ivLeft.setVisibility(GONE);
            ivRight.setVisibility(VISIBLE);
            ivRight.setTextColor(getResources().getColor(R.color.colorPrimary));
            ivPartnerAvatar.setVisibility(View.GONE);
            ivMyAvatar.setVisibility(View.VISIBLE);
//            txtTitleRight.setVisibility(View.VISIBLE);
//            txtTitleLeft.setVisibility(View.GONE);
        } else {
            tvMessage.setBackgroundResource(R.drawable.border_color_divider);
            llMessage.setBackgroundResource(R.drawable.border_color_divider);
            tvMessage.setTextColor(getResources().getColor(R.color.primaryText));
            vLeft.setVisibility(GONE);
            vRight.setVisibility(VISIBLE);
            ivLeft.setVisibility(VISIBLE);
            ivRight.setVisibility(GONE);
            ivLeft.setTextColor(getResources().getColor(R.color.divider));
            ivPartnerAvatar.setVisibility(View.VISIBLE);
            ivMyAvatar.setVisibility(View.GONE);
//            txtTitleRight.setVisibility(View.GONE);
//            txtTitleLeft.setVisibility(View.VISIBLE);
        }

        setType(TYPE_MESSAGE);
    }

    public void setType(int type) {
        switch (type) {
            case TYPE_MESSAGE:
                tvMessage.setVisibility(VISIBLE);
                ivLocation.setVisibility(GONE);
                break;
            case TYPE_IMAGE:
                tvMessage.setVisibility(GONE);
                ivLocation.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    public void setMessage(String message) {
        tvMessage.setText(message);
    }

    public void setTitle(String title) {
        if (isMine) {
            txtTitleRight.setText(title);
        } else {
            txtTitleLeft.setText(title);
        }
    }

    public void removeAvatar() {
        //xóa avatar của mình
        Glide.with(getContext())
                .load(R.drawable.img_white)
                // .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.img_white)
                .placeholder(R.drawable.img_white)
                .into(ivMyAvatar);
        //xóa avatar người chat
        Glide.with(getContext())
                .load(R.drawable.img_white)
                // .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.img_white)
                .placeholder(R.drawable.img_white)
                .into(ivPartnerAvatar);
    }

    public void setAvatar(String url) {
        if (isMine) {
            Glide.with(getContext())
                    .load(url)
                    // .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .error(R.drawable.avatar)
                    .placeholder(R.drawable.place_holder_gallery)
                    .into(ivMyAvatar);
        } else {
            Glide.with(getContext())
                    .load(url)
                    //.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .error(R.drawable.avatar)
                    .placeholder(R.drawable.place_holder_gallery)
                    .into(ivPartnerAvatar);
        }
    }

    public void setImage(String filePath) {

        Glide.with(getContext())
                .load(filePath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.place_holder_gallery)
                .into(ivLocation);
    }

    public void setOwner(boolean isOwner) {
        this.isMine = isOwner;
        update();
    }
}
