package com.itute.dating.chat.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;
import com.itute.dating.R;
import com.itute.dating.base.view.IconTextView;
import com.itute.dating.util.Constants;

/**
 * Created by manh on 6/15/2016.
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

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_MAPS = 1;

    private static final int MAPS_ZOOM = 17;


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
        LayoutInflater.from(getContext()).inflate(R.layout.item_view_chat, this, true);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        vLeft = findViewById(R.id.v_left);
        vRight = findViewById(R.id.v_right);
        ivLeft = (IconTextView) findViewById(R.id.iv_left_arrow);
        ivRight = (IconTextView) findViewById(R.id.iv_right_arrow);
        ivLocation = (ImageView) findViewById(R.id.iv_location);
        llMessage = findViewById(R.id.ll_message);

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
        } else {
            tvMessage.setBackgroundResource(R.drawable.border_color_divider);
            llMessage.setBackgroundResource(R.drawable.border_color_divider);
            tvMessage.setTextColor(getResources().getColor(R.color.primaryText));
            vLeft.setVisibility(GONE);
            vRight.setVisibility(VISIBLE);
            ivLeft.setVisibility(VISIBLE);
            ivRight.setVisibility(GONE);
            ivLeft.setTextColor(getResources().getColor(R.color.divider));
        }

        setType(TYPE_MESSAGE);
    }

    public void setOwner(boolean isOwner) {
        this.isMine = isOwner;
        update();
    }

    public void setMessage(String message) {
        tvMessage.setText(message);
    }

    public void setImage(String filePath) {

        Glide.with(getContext())
                .load(filePath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.place_holder_gallery)
                .into(ivLocation);
    }

    public void setType(int type) {
        switch (type) {
            case TYPE_MESSAGE:
                tvMessage.setVisibility(VISIBLE);
                ivLocation.setVisibility(GONE);
                break;
            case TYPE_MAPS:
                tvMessage.setVisibility(GONE);
                ivLocation.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    public void setLocation(final LatLng latLng) {
        if (null != latLng) {
            String url = String.format(Constants.DEFAULT_LOCALE,
                    "http://maps.google.com/maps/api/staticmap?center=%f,%f&markers=%f,%f&zoom=%d&size=%dx%d&sensor=false",
                    latLng.latitude,
                    latLng.longitude,
                    latLng.latitude,
                    latLng.longitude,
                    MAPS_ZOOM,
                    getResources().getDimensionPixelOffset(R.dimen.size_maps_width),
                    getResources().getDimensionPixelOffset(R.dimen.size_maps_height));

            Glide.with(getContext())
                    .load(url)
                    .error(R.mipmap.ic_launcher)
                    .centerCrop()
                    .into(ivLocation);

            ivLocation.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(String.format(Constants.DEFAULT_LOCALE, "geo:%f,%f?q=%f,%f?z=%d",
                                    latLng.latitude,
                                    latLng.longitude,
                                    latLng.latitude,
                                    latLng.longitude,
                                    MAPS_ZOOM)));
                    getContext().startActivity(intent);
                }
            });
        }
    }
}
