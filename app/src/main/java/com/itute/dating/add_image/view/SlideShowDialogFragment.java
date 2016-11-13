package com.itute.dating.add_image.view;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itute.dating.R;
import com.itute.dating.add_image.MyViewPagerAdapter;
import com.itute.dating.add_image.model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 12/11/2016.
 */
public class SlideShowDialogFragment extends DialogFragment {
    private String TAG = "SlideShowDialogFragment";
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView txtCount;
    private int selectedPosition = 0;

    static SlideShowDialogFragment newInstance() {
        SlideShowDialogFragment f = new SlideShowDialogFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        txtCount = (TextView) rootView.findViewById(R.id.txtCount);

        images = (ArrayList<Image>) getArguments().getSerializable("images");
        selectedPosition = getArguments().getInt("position");
        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        myViewPagerAdapter = new MyViewPagerAdapter(getActivity(), images);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                displayInfo(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setCurrentItem(selectedPosition);
        return rootView;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayInfo(position);
    }

    private void displayInfo(int position) {
        txtCount.setText(String.format("%d of %d", (position + 1), images.size()));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
}
