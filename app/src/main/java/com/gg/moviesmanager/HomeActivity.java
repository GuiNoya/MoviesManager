package com.gg.moviesmanager;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.Date;

/**
 * Main Activity.
 * Creates the tabs, pages, associates the movies lists with each page.
 * Manages the first run (loads the data from the internet and sets the shared preferences).
 */
public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final int NUM_TABS = 5;

    private SectionPagerAdapter pagerAdapter;
    private SearchView searchView;
    private MenuItem searchMenu;
    private static HomeActivity instance;
    private SharedPreferences sharedPrefs;

    private static ProgressDialog progressDialog;
    private static int loadersRunning = 0;

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
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);

        setSupportActionBar(toolbar);

        assert viewPager != null;
        assert tabLayout != null;
        pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        sharedPrefs = getSharedPreferences("main.prefs", Context.MODE_PRIVATE);
        boolean firstRun = sharedPrefs.getBoolean("first_run", true);
        // The first time the app is executed all the data must be loaded from the internet.
        // In all others executions, the data is loaded from the local database.
        if (firstRun) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                loadAndSavePrefs();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Please, connect to the internet before continuing.")
                        .setCancelable(false);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                // Creates a thread that will wait for internet connection.
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = cm.getActiveNetworkInfo();
                        while (netInfo != null && netInfo.getState() != NetworkInfo.State.CONNECTED) {
                            try {
                                Thread.sleep(2500);
                            } catch (InterruptedException e) {
                                Log.v("MoviesManager", "Waiter thread sleep interrupted");
                            }
                            netInfo = cm.getActiveNetworkInfo();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                                loadAndSavePrefs();
                            }
                        });
                    }
                });
                t.start();
            }
        }
    }

    private void loadAndSavePrefs() {
        getLoaderManager().initLoader(0, null, this).forceLoad();
        getLoaderManager().initLoader(1, null, this).forceLoad();
        getLoaderManager().initLoader(2, null, this).forceLoad();
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean("first_run", false);
        editor.putLong("last_update", new Date().getTime());
        editor.apply();
        setAlarm();
        progressDialog = ProgressDialog.show(this, getString(R.string.loading),
                getString(R.string.please_wait_download), true, false);
    }

    /**
     * Sets an Alarm to automatically update the data (the app doesn't have to be running).
     * The time of the last update is read from the shared preferences and the alarm is set
     * to run every three days.
     */
    private void setAlarm() {
        long lastUpdate = sharedPrefs.getLong("last_update", 0);
        Intent intent = new Intent(this, AutoUpdater.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC,
                lastUpdate + AlarmManager.INTERVAL_DAY * 3,
                AlarmManager.INTERVAL_DAY * 3, pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenu = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenu.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
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
        int id = item.getItemId();
        switch (id) {
            case R.id.search:
                return super.onOptionsItemSelected(item);
            case R.id.update:
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    progressDialog = ProgressDialog.show(this, getString(R.string.loading),
                            getString(R.string.please_wait_download), true, false);
                    getLoaderManager().initLoader(0, null, this).forceLoad();
                    getLoaderManager().initLoader(1, null, this).forceLoad();
                    getLoaderManager().initLoader(2, null, this).forceLoad();
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putLong("last_update", new Date().getTime());
                    editor.apply();

                } else {
                    Toast.makeText(this, "No internet connection!", Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DataBaseConnection.getInstance(this).close();
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new FullLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        if (loadersRunning > 0) {
            loadersRunning--;
        }
        pagerAdapter.reloadAdapter(loader.getId());
        if (loadersRunning < 1 && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    /**
     * Loads the movie list.
     * The loader is associated with each type of list.
     */
    public static class FullLoader extends AsyncTaskLoader<ArrayList<Movie>> {
        public FullLoader(Context context) {
            super(context);
        }

        @Override
        public ArrayList<Movie> loadInBackground() {
            ArrayList<Movie> results;

            String jsonString = "";
            // Checks which list should be loaded.
            if (getId() == 0) {
                jsonString = DataDownloader.getLatest();
            } else if (getId() == 1) {
                jsonString = DataDownloader.getUpcoming();
            } else if (getId() == 2) {
                jsonString = DataDownloader.getPopulars();
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
                    DbCrud.getInstance(getContext()).insertMovies(results);
                    for (Movie m : results) {
                        if (!m.getPoster().equals("")) {
                            DataDownloader.downloadImage(getContext(), m.getPoster(), DataDownloader.TypeImage.POSTER);
                        }
                    }
                }
            }

            return results;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            loadersRunning++;
            forceLoad();
        }
    }

    /**
     * FragmentPagerAdapter associated with the tabs.
     */
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
