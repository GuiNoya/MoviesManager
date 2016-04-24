package com.gg.moviesmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MovieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Movie movie = (Movie) getIntent().getSerializableExtra("movie");

        ImageView imgCover = (ImageView) findViewById(R.id.movie_image);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        TextView tvName = (TextView) findViewById(R.id.movie_name);
        TextView tvReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        CheckBox cbWatchlist = (CheckBox) findViewById(R.id.cb_watchlist);
        CheckBox cbWatched = (CheckBox) findViewById(R.id.cb_watched);
        ImageView imgShare = (ImageView) findViewById(R.id.share);
        ImageView imgTrailer = (ImageView) findViewById(R.id.trailer);
        TextView tvSynopsis = (TextView) findViewById(R.id.synopsis);
        TextView tvGenres = (TextView) findViewById(R.id.genres);
        TextView tvRated = (TextView) findViewById(R.id.rated);
        TextView tvRuntime = (TextView) findViewById(R.id.runtime);
        TextView tvDirector = (TextView) findViewById(R.id.director);
        TextView tvCast = (TextView) findViewById(R.id.cast);

        assert ratingBar != null;
        assert tvName != null;
        assert tvReleaseDate != null;
        ratingBar.setRating(movie.getGrade()/2);
        tvName.setText(movie.getTitle());
        tvReleaseDate.setText(movie.getReleaseDate());
    }

}
