package com.gg.moviesmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Activity that show a movie page.
 */
public class MovieActivity extends AppCompatActivity {
    private Movie movie;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share:
                // Creates an Intent to share the movie over an app or service selected by the user.
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, movie.getTitle() +
                        "\nhttps://www.themoviedb.org/movie/" + movie.getId());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_via)));
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // If movie is already in memory or have to be loaded from the database.
        final int movieId = getIntent().getIntExtra("movie_id", -1);
        Movie movie = Movie.getMovie(movieId);
        if (movie == null) {
            movie = DbCrud.getInstance(this).selectMovie(movieId);
            assert movie != null;
        }

        loadUi(movie);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!movie.isLoaded()) {
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                // If the movie data is incomplete and there is internet connection,
                // creates a thread to download and update the data.
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String jsonString = DataDownloader.getMovie(movieId);
                        if ("".equals(jsonString)) {
                            Toast.makeText(MovieActivity.this, "Error downloading movie!", Toast.LENGTH_SHORT).show();
                            Log.e("MovieDownload", "Could not download movie!");
                        } else {
                            Movie m = JSONParser.parseMovie(jsonString);
                            if (jsonString == null) {
                                Toast.makeText(MovieActivity.this, "Error parsing movie!", Toast.LENGTH_SHORT).show();
                                Log.e("MovieDownload", "Could not parse movie!");
                            } else {
                                // Update database with new data.
                                DbCrud.getInstance(MovieActivity.this).updateMovie(m, false);
                                // Load the new data, and update the UI.
                                final Movie m2 = DbCrud.getInstance(MovieActivity.this).selectMovie(movieId);
                                DataDownloader.downloadImage(MovieActivity.this, m.getBackdrop(), DataDownloader.TypeImage.BACKDROP);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadUi(m2);
                                    }
                                });
                            }
                        }
                    }
                });
                t.start();
            } else {
                ImageView imgTrailer = (ImageView) findViewById(R.id.trailer);
                assert imgTrailer != null;
                imgTrailer.setVisibility(View.GONE);
                Toast.makeText(this, "Data incomplete. No internet access.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Reads from the Movie object and writes on the UI.
    private void loadUi(Movie mov) {
        this.movie = mov;
        ImageView imgCover = (ImageView) findViewById(R.id.movie_image);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        TextView tvReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        CheckBox cbWatchlist = (CheckBox) findViewById(R.id.cb_watchlist);
        CheckBox cbWatched = (CheckBox) findViewById(R.id.cb_watched);
        ImageView imgTrailer = (ImageView) findViewById(R.id.trailer);
        TextView tvSynopsis = (TextView) findViewById(R.id.synopsis);
        TextView tvGenres = (TextView) findViewById(R.id.genres_content);
        TextView tvRuntime = (TextView) findViewById(R.id.runtime_content);
        TextView tvLanguage = (TextView) findViewById(R.id.language_content);
        TextView tvDirector = (TextView) findViewById(R.id.director_content);
        TextView tvCast = (TextView) findViewById(R.id.cast_content);

        assert ratingBar != null;
        assert tvReleaseDate != null;
        assert cbWatchlist != null;
        assert cbWatched != null;
        assert tvSynopsis != null;
        assert tvRuntime != null;
        assert tvDirector != null;
        assert tvCast != null;
        assert tvGenres != null;
        assert tvLanguage != null;
        assert imgCover != null;
        assert imgTrailer != null;

        //noinspection ConstantConditions
        getSupportActionBar().setTitle(movie.getTitle());

        ratingBar.setRating(movie.getRating() / 2);
        tvReleaseDate.setText(movie.getReleaseDate());
        cbWatchlist.setChecked(movie.isWatchlist());
        cbWatched.setChecked(movie.isWatched());
        tvSynopsis.setText(movie.getOverview());
        tvRuntime.setText(String.valueOf(movie.getRuntime()));
        tvDirector.setText(movie.getDirector());
        tvCast.setText(movie.getCast());
        tvLanguage.setText(movie.getLanguage().toUpperCase());
        String genres = "";
        for (String s : movie.getGenres().values()) {
            genres += s + ", ";
        }
        if (!genres.equals("")) {
            genres = genres.substring(0, genres.length() - 2);
        }
        tvGenres.setText(genres);

        if ("".equals(movie.getTrailer())) {
            imgTrailer.setVisibility(View.GONE);
        } else {
            imgTrailer.setVisibility(View.VISIBLE);
        }

        if (!movie.getBackdrop().equals("")) {
            File f = getFileStreamPath(movie.getBackdrop());
            if (f.exists()) {
                try {
                    FileInputStream fs = openFileInput(movie.getBackdrop());
                    Bitmap img = BitmapFactory.decodeStream(fs);
                    fs.close();
                    imgCover.setImageBitmap(img);
                } catch (IOException e) {
                    Log.e("ListAdapter", "Could not load image file");
                }
            }
        }

        cbWatched.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    movie.setWatched(true);
                    DbCrud.getInstance(MovieActivity.this).setWatched(movie.getId(), true);
                } else {
                    movie.setWatched(false);
                    DbCrud.getInstance(MovieActivity.this).setWatched(movie.getId(), false);
                }
                HomeActivity.getInstance().getPagerAdapter().reloadAdapter(4);
            }
        });
        cbWatchlist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    movie.setWatchlist(true);
                    DbCrud.getInstance(MovieActivity.this).setWatchlist(movie.getId(), true);
                } else {
                    movie.setWatchlist(false);
                    DbCrud.getInstance(MovieActivity.this).setWatchlist(movie.getId(), false);
                }
                HomeActivity.getInstance().getPagerAdapter().reloadAdapter(3);
            }
        });

        imgTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ytId = movie.getTrailer();
                if (!"".equals(ytId)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ytId));
                    startActivity(intent);
                }
            }
        });
    }
}