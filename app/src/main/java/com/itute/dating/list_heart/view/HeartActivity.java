package com.itute.dating.list_heart.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.util.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 03/11/2016.
 */
public class HeartActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btnAppBack)
    TextView txtBack;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.container)
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        ButterKnife.bind(this);
        createFragment();
        //event click
        txtBack.setOnClickListener(this);

    }

    private void createFragment() {
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ListHeartFragment(), getResources().getString(R.string.tim));
        viewPagerAdapter.addFragment(new ListFriendFragment(), getResources().getString(R.string.banBe));
        viewPager.setAdapter(viewPagerAdapter);
        // cache
        viewPager.setOffscreenPageLimit(1);
    }

    @Override
    public void onClick(View view) {
        if (view == txtBack) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}
