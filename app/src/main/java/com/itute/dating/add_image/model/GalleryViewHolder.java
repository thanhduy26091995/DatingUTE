package com.itute.dating.add_image.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.itute.dating.R;

/**
 * Created by buivu on 11/11/2016.
 */
public class GalleryViewHolder extends RecyclerView.ViewHolder {
    public ImageView thumbnail;

    public GalleryViewHolder(View itemView) {
        super(itemView);
        thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
    }
}
