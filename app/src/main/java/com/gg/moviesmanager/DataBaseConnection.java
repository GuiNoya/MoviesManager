package com.gg.moviesmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class DataBaseConnection extends SQLiteOpenHelper{

    private static final String DB_NAME = "main.db";
    private static final int DB_VERSION = '1';
    private static DataBaseConnection instance;

    private static String T_MOVIE = "Movie";
    private static String T_GENRE = "Genre";
    private static String T_MOVIEGENRE = "Movies_Genres";

    private static String C_ID = "_id";
    private static String C_TITLE = "title";

    private DataBaseConnection(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DataBaseConnection getInstance(Context context) {
        if (instance == null) {
            instance = new DataBaseConnection(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Movie (_id INTEGER PRIMARY KEY, title TEXT NOT NULL, " +
                "release_date INTEGER, grade REAL, overview TEXT, language TEXT, rating TEXT, " +
                "runtime INTEGER, cast TEXT, director TEXT, watchlist INTEGER, watched INTEGER);");

        db.execSQL("CREATE TABLE Genre (_id INTEGER PRIMARY KEY, name TEXT UNIQUE NOT NULL);");

        db.execSQL("CREATE TABLE Movies_Genres (_id INTEGER PRIMARY KEY, movie_id INTEGER, " +
                "genre_id INTEGER, FOREIGN KEY(movie_id) REFERENCES Movie(_id), " +
                "FOREIGN KEY(genre_id) REFERENCES Genre(_id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Movie");
        db.execSQL("DROP TABLE IF EXISTS Genre");
        db.execSQL("DROP TABLE IF EXISTS Movies_Genres");
        onCreate(db);
    }

    public void insertMovie(Movie m){

    }

    public void updateMovie(Movie m){

    }

    public void deleteMovie(Movie m){

    }

    public void insertGenre(int id, String name){
        ContentValues cv = new ContentValues();

        cv.put(C_ID, id);
        cv.put("name", name);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(T_GENRE, null, cv);
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