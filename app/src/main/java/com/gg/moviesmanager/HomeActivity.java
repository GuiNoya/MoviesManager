package com.gg.moviesmanager;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class HomeActivity extends AppCompatActivity {
    private static final int NUM_TABS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);

        setSupportActionBar(toolbar);

        assert viewPager != null;
        assert tabLayout != null;
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MoviesListFragment.newInstance();
                case 1:
                    return MoviesListFragment.newInstance();
                case 2:
                    return MoviesListFragment.newInstance();
                case 3:
                    return MoviesListFragment.newInstance();
                default:
                    return MoviesListFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.in_theaters);
                case 1:
                    return getResources().getString(R.string.upcoming);
                case 2:
                    return getResources().getString(R.string.populars);
                case 3:
                    return getResources().getString(R.string.watchlist);
                default:
                    return null;
            }
        }
    }
}
