package com.gg.moviesmanager;

import android.app.SearchManager;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, DataConnection.AsyncAccessResult {
    private static final int NUM_TABS = 5;
    private ViewPager viewPager;
    private SearchView searchView;
    private MenuItem searchMenu;

    @Override
    public void accessResult(String asyncResult) {
        Log.i("r", asyncResult);
        new JSONParser(asyncResult);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.main_pager);

        setSupportActionBar(toolbar);

        assert viewPager != null;
        assert tabLayout != null;
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);

        DataConnection connection = new DataConnection();
        connection.resultAccess = this;
        connection.execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenu = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenu.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setOnQueryTextListener(this);
        //searchView.setIconifiedByDefault(false);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        /*if (query.charAt(0) != ':' && !query.startsWith("Watchlist:")) {
            if (viewPager.getCurrentItem() == 3)
                query = "Watchlist:" + query;
            else
                query = ":" + query;
            searchView.setQuery(query, true);
            return true;
        }*/
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        searchView.setQuery("", false);
        searchView.setIconified(false);
        searchMenu.collapseActionView();
        searchView.clearFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*int id = item.getItemId();
        switch (id) {
            case R.id.action_search:

        }*/
        return super.onOptionsItemSelected(item);
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
                case 4:
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
                case 4:
                    return getResources().getString(R.string.watched);
                default:
                    return null;
            }
        }
    }
}
