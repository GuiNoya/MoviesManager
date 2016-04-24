package com.gg.moviesmanager;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultsActivity extends AppCompatActivity {

    private ListView listView;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        listView = (ListView) findViewById(android.R.id.list);
        tvEmpty = (TextView) findViewById(android.R.id.empty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
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
            String query = intent.getStringExtra(SearchManager.QUERY);
            ((EditText) findViewById(R.id.editText)).setText(query);
            //use the query to search your data somehow
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Toast.makeText(this, "ACTION_VIEW", Toast.LENGTH_SHORT).show();
        }

        final Movie[] values = {new Movie("Test 1", 6, "12/12/1212"), new Movie("Test 2", 3, "21/21/2121")};
        final MovieListAdapter mva = new MovieListAdapter(this, R.layout.movie_list_item, values);
        listView.setAdapter(mva);
        listView.requestFocus();
        if (mva.getCount() > 0) {
            listView.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }
}
