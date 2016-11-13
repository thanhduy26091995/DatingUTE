package com.itute.dating.add_image;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itute.dating.R;
import com.itute.dating.add_image.model.GalleryViewHolder;
import com.itute.dating.add_image.model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 11/11/2016.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryViewHolder> {

    private ArrayList<Image> images;
    private Context mContext;

    public GalleryAdapter(ArrayList<Image> images, Context mContext) {
        this.images = images;
        this.mContext = mContext;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_thumbnail, parent, false);

        return new GalleryViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        Image image = images.get(position);

        Glide.with(mContext)
                .load(image.getImageUrl())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void clearAllImage() {
        images.clear();
        notifyDataSetChanged();
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context mContext, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View v = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (v != null && clickListener != null) {
                        clickListener.onLongClick(v, recyclerView.getChildPosition(v));
                    }
                }
            });
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
