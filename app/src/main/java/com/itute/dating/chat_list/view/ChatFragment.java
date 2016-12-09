package com.itute.dating.chat_list.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itute.dating.R;

/**
 * Created by buivu on 05/12/2016.
 */
public class ChatFragment extends Fragment {
    private TabLayout tabLayout;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_all_conservation, container, false);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

        //set up for tab
        setUpTab();
        return rootView;
    }

    //init Home Tab
    private void initTab() {
        Fragment chatLisFragment = new ChatListFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        getActivity().getSupportFragmentManager().popBackStack();
        chatLisFragment.setArguments(null);
        ft.replace(R.id.content, chatLisFragment);
        ft.commitAllowingStateLoss();
    }

    private void setUpTab() {
        //add two title tab into tab layout
        tabLayout.addTab(tabLayout.newTab().setText(R.string.singleChat));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.chatGroup));
        //setupTabTextAndIcons();
        //init home page tab
        initTab();
        //event selectedChange tab
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 1) {
                    Fragment chatGroupFragment = new ChatGroupFragment();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    getActivity().getSupportFragmentManager().popBackStack();
                    chatGroupFragment.setArguments(null);
                    ft.replace(R.id.content, chatGroupFragment);
                    ft.commitAllowingStateLoss();
                } else if (position == 0) {
                    Fragment chatLisFragment = new ChatListFragment();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    getActivity().getSupportFragmentManager().popBackStack();
                    chatLisFragment.setArguments(null);
                    ft.replace(R.id.content, chatLisFragment);
                    ft.commitAllowingStateLoss();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
