package com.itute.dating.main.view;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.itute.dating.R;
import com.itute.dating.add_friend.view.AddFriendFragment;
import com.itute.dating.base.model.DeviceToken;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.base.view.GoogleAuthController;
import com.itute.dating.chat_list.view.ChatFragment;
import com.itute.dating.first_login.view.FirstLoginActivity;
import com.itute.dating.main.presenter.MainPresenter;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.settings.view.SettingsFragment;
import com.itute.dating.sign_in.view.SignInActivity;
import com.itute.dating.user_list.view.UserListFragment;
import com.itute.dating.util.Constants;
import com.itute.dating.util.FontManager;
import com.itute.dating.util.ViewPagerAdapter;

import bolts.AppLinks;

/**
 * Created by buivu on 09/10/2016.
 */
public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private static final String TAG = "MainActivity";
    private DatabaseReference mUserReference;
    private MainPresenter presenter;
    private int gender;
    private DatabaseReference mDatabase;
    private AdView avBanner;
    private AdRequest adRequest;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        }
        setContentView(R.layout.activity_main);
        //init
        presenter = new MainPresenter(this);
        mUserReference = FirebaseDatabase.getInstance().getReference();
        //end init
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            adView();
            initInfo();
            //add device token
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String token = FirebaseInstanceId.getInstance().getToken();
            DeviceToken.getInstance().addDeviceToken(mDatabase, getUid(), token);
        }

    }

    private void adView() {
        avBanner = (AdView) findViewById(R.id.av_banner);
        adRequest = new AdRequest.Builder().build();
        // adRequest = new AdRequest.Builder().addTestDevice("C8F1047E8915C2CF2ECC934BF26F844C").build();
        avBanner.loadAd(adRequest);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAuthController.install(this, this);
    }

    private void initInfo() {
        showProgessDialog();
        try {
            mUserReference = presenter.getUser(getUid());
            mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            gender = user.getGender();
                            if (gender != 0) {
                                createFragment();
                            } else {
                                startActivity(new Intent(MainActivity.this, FirstLoginActivity.class));
                                finish();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                            hideProgressDialog();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            hideProgressDialog();
        }
    }

    //create fragment and set up font-awesome
    private void createFragment() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        setupViewPagerUser(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryDark));
        setupTabTextAndIcons();
        mViewPager.setOffscreenPageLimit(3);
        setSelectedTab();


//        broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)){
//                    String message = intent.getStringExtra("message");
//                    Log.d(TAG, message);
//                }
//            }
//        };

    }

    private void setupTabTextAndIcons() {
        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);

        //Tab UserList
        LinearLayout tabViewOne = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_icon, null);
        ((TextView) tabViewOne.findViewById(R.id.tabIcon)).setTypeface(iconFont);
        ((TextView) tabViewOne.findViewById(R.id.tabIcon)).setText(R.string.fa_list_alt);
        tabLayout.getTabAt(0).setCustomView(tabViewOne);
        //Tab Chat List
        LinearLayout tabViewTwo = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_icon, null);
        ((TextView) tabViewTwo.findViewById(R.id.tabIcon)).setTypeface(iconFont);
        ((TextView) tabViewTwo.findViewById(R.id.tabIcon)).setText(R.string.fa_comments_o);
        tabLayout.getTabAt(1).setCustomView(tabViewTwo);
        //Tab Chat List
        LinearLayout tabViewThree = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_icon, null);
        ((TextView) tabViewThree.findViewById(R.id.tabIcon)).setTypeface(iconFont);
        ((TextView) tabViewThree.findViewById(R.id.tabIcon)).setText(R.string.fa_user_plus);
        tabLayout.getTabAt(2).setCustomView(tabViewThree);
        //Tab Settings
        LinearLayout tabViewFour = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_icon, null);
        ((TextView) tabViewFour.findViewById(R.id.tabIcon)).setTypeface(iconFont);
        ((TextView) tabViewFour.findViewById(R.id.tabIcon)).setText(R.string.fa_bars);
        tabLayout.getTabAt(3).setCustomView(tabViewFour);

    }

    private void setupViewPagerUser(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UserListFragment(), getString(R.string.fa_list_alt));
        adapter.addFragment(new ChatFragment(), getString(R.string.fa_comments_o));
        adapter.addFragment(new AddFriendFragment(), getString(R.string.fa_user_plus));
        adapter.addFragment(new SettingsFragment(), getString(R.string.fa_bars));
        viewPager.setAdapter(adapter);
    }

    public void setSelectedTab() {
        // Fetch the selected tab index with default
        int selectedTabIndex = getIntent().getIntExtra(Constants.TAB_SELECT, 0);

        Log.d(TAG, "" + selectedTabIndex);
        // Switch to page based on index
        mViewPager.setCurrentItem(selectedTabIndex);
    }

}
