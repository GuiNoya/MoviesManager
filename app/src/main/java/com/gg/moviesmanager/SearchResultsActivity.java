package com.gg.moviesmanager;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private LinearLayout mainLayout;
    private ListView listView;
    private TextView tvEmpty;
    private EditText etQuery;
    private MovieListAdapter adapter;
    private ProgressDialog progressDialog;
    private static String query;
    private static boolean internet = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        listView = (ListView) findViewById(android.R.id.list);
        tvEmpty = (TextView) findViewById(android.R.id.empty);
        etQuery = (EditText) findViewById(R.id.query);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.wtf("SEARCH", "onNewIntent");
        setIntent(intent);
        listView = (ListView) findViewById(android.R.id.list);
        tvEmpty = (TextView) findViewById(android.R.id.empty);
        etQuery = (EditText) findViewById(R.id.query);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handleIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            final EditText editText = (EditText) findViewById(R.id.query);
            assert editText != null;
            editText.setText(query);
            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        int value = editText.getRight() -
                                editText.getCompoundDrawables()[2].getBounds().width();
                        Log.wtf("TOUCH", "raw: " + event.getRawX());
                        Log.wtf("TOUCH", "v: " + value);
                        if(event.getRawX() >= value) {
                            Log.wtf("TOUCH", "ENTROU");
                            mainLayout.requestFocus();
                            String s = editText.getText().toString();
                            makeSearch(s);
                            return true;
                        }
                    }
                    return false;
                }
            });
            /*editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        mainLayout.requestFocus();
                        makeSearch(v.getText().toString());
                        return true;
                    }
                    return false;
                }
            });*/
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(SearchResultsActivity.this, MovieActivity.class);
                    Movie obj = (Movie) adapter.getItem(position);
                    intent.putExtra("movie_id", obj.getId());
                    startActivity(intent);
                }
            });
            adapter = new MovieListAdapter(this, R.layout.movie_list_item);
            listView.setAdapter(adapter);
            mainLayout.requestFocus();
            makeSearch(query);
        } else {
            Log.wtf("SearchResultsActivity", "Intent action is not SEARCH, it is: " + intent.getAction());
            throw new RuntimeException("This view should only be used with with ACTION_VIEW");
        }
    }

    private void makeSearch(String query) {
        progressDialog = ProgressDialog.show(this, getString(R.string.searching), getString(R.string.please_wait), true, false);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        internet = netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED;
        SearchResultsActivity.query = query;
        getLoaderManager().initLoader(100, null, this).forceLoad();
        refreshList();
    }

    private void refreshList() {
        if (adapter.getCount() > 0) {
            listView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        mainLayout.requestFocus();
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        Log.wtf("SEARCH LOADER", "CREATING");
        return new SearchLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        Log.wtf("SEARCH LOADER", "FINISHED");
        adapter.clear();
        adapter.addAll(data);
        refreshList();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            if (!internet) {
                Toast.makeText(this, R.string.search_no_internet, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        Log.wtf("SEARCH LOADER", "RESET");
    }

    public static class SearchLoader extends AsyncTaskLoader<ArrayList<Movie>> {
        public SearchLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public ArrayList<Movie> loadInBackground() {
            if (SearchResultsActivity.internet) {
                Log.wtf("SEARCH LOADER", "SEARCHING WITH INTERNET");
                ArrayList<Movie> results;
                String jsonString = DataDownloader.getSearch(SearchResultsActivity.query);
                if (jsonString.equals("")) {
                    Toast.makeText(getContext(), "Error while searching!", Toast.LENGTH_SHORT).show();
                    Log.e("MovieSearch", "Could not search!");
                    results = null;
                } else {
                    results = JSONParser.parseList(jsonString);
                    if (results == null) {
                        Toast.makeText(getContext(), "Error parsing search!", Toast.LENGTH_SHORT).show();
                        Log.e("MovieSearch", "Could not parse list!");
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
            } else {
                Log.wtf("SEARCH LOADER", "SEARCHING NO INTERNET");
                return DbCrud.getInstance(getContext()).search(SearchResultsActivity.query);
            }
        }
    }
}
