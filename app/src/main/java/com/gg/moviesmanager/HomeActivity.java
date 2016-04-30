package com.gg.moviesmanager;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final int NUM_TABS = 5;

    private ViewPager viewPager;
    private SectionPagerAdapter pagerAdapter;
    private SearchView searchView;
    private MenuItem searchMenu;
    private static HomeActivity instance;

    private static ProgressDialog progressDialog;

    public static HomeActivity getInstance() {
        return instance;
    }

    public SectionPagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        instance = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.main_pager);

        setSupportActionBar(toolbar);

        assert viewPager != null;
        assert tabLayout != null;
        pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        SharedPreferences sharedPrefs = getSharedPreferences("main.prefs", Context.MODE_PRIVATE);
        boolean firstRun = sharedPrefs.getBoolean("first_run", true);
        if (firstRun) {
            getLoaderManager().initLoader(0, null, this).forceLoad();
            getLoaderManager().initLoader(1, null, this).forceLoad();
            getLoaderManager().initLoader(2, null, this).forceLoad();
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("first_run", false);
            editor.apply();
            progressDialog = ProgressDialog.show(this, "Loading...",
                    "Please, wait while download finishes.", true, false);
        }
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

    @Override
    protected void onStop() {
        super.onStop();
        DataBaseConnection.getInstance(this).close();
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        Log.wtf("LOADER", "CREATING");
        return new FullLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        Log.wtf("LOADER", "FINISHED");
        pagerAdapter.reloadAdapter(loader.getId());
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        Log.wtf("LOADER", "RESET");
        //pagerAdapter.getFragment(loader.getId()).setListAdapter(null);
    }

    public static class FullLoader extends AsyncTaskLoader<ArrayList<Movie>> {
        public FullLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<Movie> loadInBackground() {
            ArrayList<Movie> results;

            String jsonString = "";
            if (getId() == 0) {
                jsonString = DataDownloader.getLatest(1);
            } else if (getId() == 1) {
                jsonString = DataDownloader.getUpcoming(1);
            } else if (getId() == 2) {
                jsonString = DataDownloader.getPopulars(1);
            }
            if (jsonString.equals("")) {
                Toast.makeText(getContext(), "Error downloading list!", Toast.LENGTH_SHORT).show();
                Log.e("ListDownload", "Could not download list!");
                results = null;
            } else {
                results = JSONParser.parseList(jsonString);
                if (results == null) {
                    Toast.makeText(getContext(), "Error parsing list!", Toast.LENGTH_SHORT).show();
                    Log.e("ListDownload", "Could not parse list!");
                } else {
                    DbCrud d = DbCrud.getInstance(getContext());
                    for (Movie m : results) {
                        d.insertMovie(m);
                    }
                    for (Movie m : results) {
                        if (!m.getPoster().equals("")) {
                            DataDownloader.downloadImage(getContext(), m.getPoster(), DataDownloader.TypeImage.POSTER);
                        }
                    }
                }
            }

            if (results != null) {
                return results;
            }

            return null;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        private final MoviesListFragment[] fragments = new MoviesListFragment[5];

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void reloadAdapter(final int id) {
            if (getFragment(id).isAdded()) {
                getFragment(id).reload();
            }
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.latest);
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

        public MoviesListFragment getFragment(int id) {
            if (fragments[id] == null) {
                fragments[id] = MoviesListFragment.newInstance(id, HomeActivity.this);
            }
            return fragments[id];
        }
    }
}
