package com.gg.moviesmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class DataBaseConnection extends SQLiteOpenHelper{

    public DataBaseConnection(Context context, String database_name , SQLiteDatabase.CursorFactory factory, int version) {
        super(context, database_name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Movie (id INTEGER PRIMARY KEY, title TEXT, releaseDate INTEGER, " +
                "grade REAL, overview TEXT, language TEXT, rating TEXT, runtime integer, " +
                "cast TEXT, director TEXT, " +
                "watchlist INTEGER, watched INTEGER");
        db.execSQL("CREATE TABLE Genre (name TEXT PRIMARY KET)");
        db.execSQL("CREATE TABLE Movies_Genres (id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER, genre_name TEXT," +
                "FOREIGN KEY(movie_id) REFERENCES Movie(id), FOREIGN KEY(genre_name) REFERENCES Genre(name))");
        db.execSQL("CREATE TABLE Watchlist (id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER," +
                "FOREIGN KEY(movie_id) REFERENCES Movie(id))");
        db.execSQL("CREATE TABLE Watched (id INTEGER PRIMARY KEY AUTOINCREMENT, movie_id INTEGER," +
                "FOREIGN KEY(movie_id) REFERENCES Movie(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Movie");
        db.execSQL("DROP TABLE IF EXISTS Genre");
        db.execSQL("DROP TABLE IF EXISTS Movies_Genres");
        db.execSQL("DROP TABLE IF EXISTS Watched");
        db.execSQL("DROP TABLE IF EXISTS Watchlist");
        onCreate(db);
    }

    public void insertMovie(Movie m){

    }

    public void updateMovie(Movie m){

    }

    public void deleteMovie(Movie m){

    }

    public void insertGenre(String name){

    }

    public void insertMovieGenre(String genre, Movie m){

    }

    public void insertWatchedMovie(Integer movie_id){

    }

    public void insertMovieWatchlist(Integer movie_id){

    }

    public void removeWatchedMovie(Integer movie_id){

    }

    public void removeMovieWatchlist(Integer movie_id){

    }


    public Movie getMovie(Integer id) {
        return null;
    }

    public List<Movie> getMovies() {
        return null;
    }

    public List<Movie> getMoviesbyGenre() {
        return null;
    }

    public List<Movie> getMoviesInTheather() {
        return null;
    }

    public List<Movie> getMoviesComing() {
        return null;
    }

    public List<Movie> getWatchlist() {
        return null;
    }

    public List<Movie> getWatched() {
        return null;
    }
}