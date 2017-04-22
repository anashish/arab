package com.mishwar.passanger_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mishwar.R;
import com.mishwar.passenger_fragments.CurrentMapFragment;
import com.mishwar.passenger_fragments.PassengerCancelBookingsFragment;
import com.mishwar.passenger_fragments.PassengerPreviousTripFragment;
import com.mishwar.passenger_fragments.PassengerUpcomingTripFragment;

import java.util.ArrayList;
import java.util.List;

public class PassengerMyBookingsActivity extends AppCompatActivity implements View.OnClickListener {

    private   ImageView driver_actionbar_btton_back;
    private  TextView driver_actionbarLayout_title;
    PassengerViewPagerAdapter adapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.current_icon,
            R.drawable.previous_icon,
            R.drawable.up_coming_icon,
            R.drawable.cancel_icon
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_my_bookings);

        initializeViews();
        adapter = new PassengerViewPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);


        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }
    /*------------------------------------------------------------------------------------*/
    private void initializeViews() {

        /*tvAccountEdit = (TextView) findViewById(R.id.tvAccountEdit);
        tvAccountChange = (TextView) findViewById(R.id.tvAccountChange);*/
        driver_actionbarLayout_title = (TextView) findViewById(R.id.driver_actionbarLayout_title);

        driver_actionbar_btton_back = (ImageView) findViewById(R.id.driver_actionbar_btton_back);

        driver_actionbarLayout_title.setText(getString(R.string.text_my_booking));

        driver_actionbar_btton_back.setOnClickListener(this);
    }
    /*------------------------------------------------------------------------------------*/
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }
    /*------------------------------------------------------------------------------------*/
    private void setupViewPager(ViewPager viewPager) {
        adapter.addFrag(new CurrentMapFragment(), getString(R.string.text_current));
        adapter.addFrag(new PassengerPreviousTripFragment(), getString(R.string.text_previous));
        adapter.addFrag(new PassengerUpcomingTripFragment(), getString(R.string.text_upcoming));
        adapter.addFrag(new PassengerCancelBookingsFragment(), getString(R.string.text_cancelled));
        viewPager.setAdapter(adapter);

    }
    /*------------------------------------------------------------------------------------*/
    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            /*Action perform for tripmothly*/
            case R.id.driver_actionbar_btton_back:
                onBackPressed();
                break;
        }
    }


    /*-------------------------------------------------------------------------------------------------------------------*/
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        CurrentMapFragment.createView =0;
        PassengerCancelBookingsFragment.createCancelView =0;
        PassengerPreviousTripFragment.createPreviousTripView =0;
        PassengerUpcomingTripFragment.createUpcomingTripView =0;
        startActivity(new Intent(PassengerMyBookingsActivity.this,HomeActivity.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out);
    }

//*-------------------------------------------------------------------------------------------------------------------*/

    class PassengerViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public PassengerViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return PassengerViewPagerAdapter.POSITION_NONE;
        }
    }
/*-------------------------------------------------------------------------------------------------------------------*/

}
