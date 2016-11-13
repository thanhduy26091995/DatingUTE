package com.itute.dating.add_image;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.itute.dating.R;
import com.itute.dating.add_image.model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 12/11/2016.
 */
public class MyViewPagerAdapter extends PagerAdapter {

    private ArrayList<Image> images;
    private Context context;
    private LayoutInflater layoutInflater;

    public MyViewPagerAdapter(Context context, ArrayList<Image> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_preview);
        Image image = images.get(position);

        Glide.with(context)
                .load(image.getImageUrl())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
