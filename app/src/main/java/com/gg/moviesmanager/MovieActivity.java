package com.gg.moviesmanager;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Objects;

public class MovieActivity extends AppCompatActivity {
    private Movie movie;

    public void downloadMovie(int id) {
        String jsonString = DataDownloader.getMovie(id);
        if ("".equals(jsonString)) {
            Toast.makeText(this, "Error downloading movie!", Toast.LENGTH_SHORT).show();
            Log.e("MovieDownload", "Could not download movie!");
        } else {
            Movie m = JSONParser.parseMovie(jsonString);
            if (m == null) {
                Toast.makeText(this, "Error parsing movie!", Toast.LENGTH_SHORT).show();
                Log.e("MovieDownload", "Could not parse movie!");
            } else {
                DbCrud.getInstance(this).insertMovie(m);
            }
        }
    }

    public void updateMovieInternet(int id, final Callback callback) {
        String jsonString = DataDownloader.getMovie(id);
        if ("".equals(jsonString)) {
            Toast.makeText(this, "Error downloading movie!", Toast.LENGTH_SHORT).show();
            Log.e("MovieDownload", "Could not download movie!");
        } else {
            Movie m = JSONParser.parseMovie(jsonString);
            if (jsonString == null) {
                Toast.makeText(this, "Error parsing movie!", Toast.LENGTH_SHORT).show();
                Log.e("MovieDownload", "Could not parse movie!");
            } else {
                DbCrud.getInstance(this).updateMovie(m);
                if (callback != null) callback.finished();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final int movieId = getIntent().getIntExtra("movie_id", -1);
        Movie movie = Movie.getMovie(movieId);
        if (movie == null) {
            movie = DbCrud.getInstance(this).selectMovie(movieId);
            assert movie != null;
        }

        loadUi(movie);
        if (!movie.isLoaded()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    updateMovieInternet(movieId, new Callback() {
                        @Override
                        public void finished() {
                            final Movie m = DbCrud.getInstance(MovieActivity.this).selectMovie(movieId);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadUi(m);
                                }
                            });
                        }
                    });
                }
            });
            t.start();
        }
    }

    private void loadUi(Movie mov) {
        this.movie = mov;
        ImageView imgCover = (ImageView) findViewById(R.id.movie_image);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        TextView tvName = (TextView) findViewById(R.id.movie_name);
        TextView tvReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        CheckBox cbWatchlist = (CheckBox) findViewById(R.id.cb_watchlist);
        CheckBox cbWatched = (CheckBox) findViewById(R.id.cb_watched);
        ImageView imgTrailer = (ImageView) findViewById(R.id.trailer);
        TextView tvSynopsis = (TextView) findViewById(R.id.synopsis);
        TextView tvGenres = (TextView) findViewById(R.id.genres);
        TextView tvRuntime = (TextView) findViewById(R.id.runtime);
        TextView tvDirector = (TextView) findViewById(R.id.director);
        TextView tvCast = (TextView) findViewById(R.id.cast);
        TextView tvLanguage = (TextView) findViewById(R.id.language);

        assert ratingBar != null;
        assert tvName != null;
        assert tvReleaseDate != null;
        assert cbWatchlist != null;
        assert cbWatched != null;
        assert tvSynopsis != null;
        assert tvRuntime != null;
        assert tvDirector != null;
        assert tvCast != null;
        assert tvGenres != null;
        assert tvLanguage != null;
        ratingBar.setRating(movie.getRating() / 2);
        tvName.setText(movie.getTitle());
        tvReleaseDate.setText(movie.getReleaseDate());
        cbWatchlist.setChecked(movie.isWatchlist());
        cbWatched.setChecked(movie.isWatched());
        tvSynopsis.setText(movie.getOverview());
        tvRuntime.setText(String.format(getResources().getString(R.string.runtimec), movie.getRuntime()));
        tvDirector.setText(String.format(getResources().getString(R.string.directorc), movie.getDirector()));
        tvCast.setText(String.format(getResources().getString(R.string.castc), movie.getCast()));
        tvLanguage.setText(String.format(getResources().getString(R.string.languagec), movie.getLanguage().toUpperCase()));
        String genres = "";
        for (String s : movie.getGenres().values()) {
            genres += s + ", ";
        }
        if (!genres.equals("")) {
            genres = genres.substring(0, genres.length() - 2);
        }
        tvGenres.setText(String.format(getResources().getString(R.string.genresc), genres));
    }

    private interface Callback {
        void finished();
    }
}