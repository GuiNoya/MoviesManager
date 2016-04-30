package com.gg.moviesmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbCrud {
    private static SQLiteDatabase db;
    private static Context context;
    private static DbCrud instance;

    private DbCrud(Context c) {
        context = c;
    }

    public static synchronized DbCrud getInstance(Context context) {
        if (instance == null) {
            instance = new DbCrud(context);
        }
        return instance;
    }

    public ArrayList<Movie> search(String query) {
        db = DataBaseConnection.getInstance(context).getReadableDatabase();
        String[] args = {"%" + query + "%"};
        Cursor c = db.rawQuery(ContractClass.DBQuery.QUERY_SEARCH, args);
        ArrayList<Movie> results = null;
        if (c.moveToNext()) {
            results = new ArrayList<>(20);
            do {
                int id = c.getInt(0);
                Movie m = Movie.getMovie(id);
                if (m == null) {
                    m = selectMovie(id);
                }
                results.add(m);
            } while (c.moveToNext());
        }
        c.close();
        return results;
    }

    public void insertMovie(Movie movie) {
        db = DataBaseConnection.getInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues(16);

        cv.put(ContractClass.DBEntry._ID, movie.getId());
        cv.put(ContractClass.DBEntry.C_TITLE, movie.getTitle());
        cv.put(ContractClass.DBEntry.C_RELEASE, movie.getReleaseDate());
        cv.put(ContractClass.DBEntry.C_RATING, movie.getRating());
        cv.put(ContractClass.DBEntry.C_OVERVIEW, movie.getOverview());
        cv.put(ContractClass.DBEntry.C_LANGUAGE, movie.getLanguage());
        cv.put(ContractClass.DBEntry.C_RUNTIME, movie.getRuntime());
        cv.put(ContractClass.DBEntry.C_POPULARITY, movie.getPopularity());
        cv.put(ContractClass.DBEntry.C_CAST, movie.getCast());
        cv.put(ContractClass.DBEntry.C_DIRECTOR, movie.getDirector());
        cv.put(ContractClass.DBEntry.C_TRAILER, movie.getTrailer());
        cv.put(ContractClass.DBEntry.C_IMPOSTER, movie.getPoster());
        cv.put(ContractClass.DBEntry.C_IMBACK, movie.getBackdrop());
        cv.put(ContractClass.DBEntry.C_WATCHLIST, movie.isWatchlist());
        cv.put(ContractClass.DBEntry.C_WATCHED, movie.isWatched());
        cv.put(ContractClass.DBEntry.C_LOADED, movie.isLoaded());

        db.beginTransaction();
        db.insertWithOnConflict(ContractClass.DBEntry.T_MOVIE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);

        ContentValues cvGenre = new ContentValues(2);

        if (movie.getGenres() != null) {
            for (Map.Entry<Integer, String> e : movie.getGenres().entrySet()) {
                cvGenre.clear();
                cvGenre.put(ContractClass.DBEntry._ID, e.getKey());
                cvGenre.put(ContractClass.DBEntry.C_NAME, e.getValue());
                db.insertWithOnConflict(ContractClass.DBEntry.T_GENRE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                cvGenre.clear();
                cvGenre.put(ContractClass.DBEntry.C_FK_MOVIE, movie.getId());
                cvGenre.put(ContractClass.DBEntry.C_FK_GENRE, e.getKey());
                db.insertWithOnConflict(ContractClass.DBEntry.T_MOVIES_GENRES, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void insertMovies(List<Movie> movies) {
        db = DataBaseConnection.getInstance(context).getWritableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues(16);
        ContentValues cvGenre = new ContentValues(2);

        for (Movie movie : movies) {
            cv.clear();
            cv.put(ContractClass.DBEntry._ID, movie.getId());
            cv.put(ContractClass.DBEntry.C_TITLE, movie.getTitle());
            cv.put(ContractClass.DBEntry.C_RELEASE, movie.getReleaseDate());
            cv.put(ContractClass.DBEntry.C_RATING, movie.getRating());
            cv.put(ContractClass.DBEntry.C_OVERVIEW, movie.getOverview());
            cv.put(ContractClass.DBEntry.C_LANGUAGE, movie.getLanguage());
            cv.put(ContractClass.DBEntry.C_RUNTIME, movie.getRuntime());
            cv.put(ContractClass.DBEntry.C_POPULARITY, movie.getPopularity());
            cv.put(ContractClass.DBEntry.C_CAST, movie.getCast());
            cv.put(ContractClass.DBEntry.C_DIRECTOR, movie.getDirector());
            cv.put(ContractClass.DBEntry.C_TRAILER, movie.getTrailer());
            cv.put(ContractClass.DBEntry.C_IMPOSTER, movie.getPoster());
            cv.put(ContractClass.DBEntry.C_IMBACK, movie.getBackdrop());
            cv.put(ContractClass.DBEntry.C_WATCHLIST, movie.isWatchlist());
            cv.put(ContractClass.DBEntry.C_WATCHED, movie.isWatched());
            cv.put(ContractClass.DBEntry.C_LOADED, movie.isLoaded());

            db.insertWithOnConflict(ContractClass.DBEntry.T_MOVIE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);

            if (movie.getGenres() != null) {
                for (Map.Entry<Integer, String> e : movie.getGenres().entrySet()) {
                    cvGenre.clear();
                    cvGenre.put(ContractClass.DBEntry._ID, e.getKey());
                    cvGenre.put(ContractClass.DBEntry.C_NAME, e.getValue());
                    db.insertWithOnConflict(ContractClass.DBEntry.T_GENRE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                    cvGenre.clear();
                    cvGenre.put(ContractClass.DBEntry.C_FK_MOVIE, movie.getId());
                    cvGenre.put(ContractClass.DBEntry.C_FK_GENRE, e.getKey());
                    db.insertWithOnConflict(ContractClass.DBEntry.T_MOVIES_GENRES, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
                }
            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void updateMovie(Movie movie, boolean fullUpdate) {
        db = DataBaseConnection.getInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues(15);

        if (fullUpdate) {
            cv.put(ContractClass.DBEntry.C_TITLE, movie.getTitle());
            cv.put(ContractClass.DBEntry.C_RELEASE, movie.getReleaseDate());
            cv.put(ContractClass.DBEntry.C_RATING, movie.getRating());
            cv.put(ContractClass.DBEntry.C_OVERVIEW, movie.getOverview());
            cv.put(ContractClass.DBEntry.C_LANGUAGE, movie.getLanguage());
            cv.put(ContractClass.DBEntry.C_POPULARITY, movie.getPopularity());
            cv.put(ContractClass.DBEntry.C_IMPOSTER, movie.getPoster());
            cv.put(ContractClass.DBEntry.C_IMBACK, movie.getBackdrop());
        }
        cv.put(ContractClass.DBEntry.C_RUNTIME, movie.getRuntime());
        cv.put(ContractClass.DBEntry.C_CAST, movie.getCast());
        cv.put(ContractClass.DBEntry.C_DIRECTOR, movie.getDirector());
        cv.put(ContractClass.DBEntry.C_TRAILER, movie.getTrailer());
        cv.put(ContractClass.DBEntry.C_LOADED, movie.isLoaded());

        db.beginTransaction();
        db.updateWithOnConflict(ContractClass.DBEntry.T_MOVIE, cv, ContractClass.DBEntry._ID +
                " = " + movie.getId(), null, SQLiteDatabase.CONFLICT_IGNORE);

        db.delete(ContractClass.DBEntry.T_MOVIES_GENRES, ContractClass.DBEntry.C_FK_MOVIE + " = " +
                movie.getId(), null);
        ContentValues cvGenre = new ContentValues(2);
        if (movie.getGenres() != null) {
            for (Map.Entry<Integer, String> e : movie.getGenres().entrySet()) {
                cvGenre.clear();
                cvGenre.put(ContractClass.DBEntry._ID, e.getKey());
                cvGenre.put(ContractClass.DBEntry.C_NAME, e.getValue());
                db.insertWithOnConflict(ContractClass.DBEntry.T_GENRE, null, cvGenre, SQLiteDatabase.CONFLICT_IGNORE);
                cvGenre.clear();
                cvGenre.put(ContractClass.DBEntry.C_FK_MOVIE, movie.getId());
                cvGenre.put(ContractClass.DBEntry.C_FK_GENRE, e.getKey());
                db.insertWithOnConflict(ContractClass.DBEntry.T_MOVIES_GENRES, null, cvGenre, SQLiteDatabase.CONFLICT_IGNORE);
            }
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void setWatched(int movieId, boolean watched) {
        db = DataBaseConnection.getInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues(1);
        cv.put(ContractClass.DBEntry.C_WATCHED, watched);
        db.update(ContractClass.DBEntry.T_MOVIE, cv, ContractClass.DBEntry._ID + " = " + movieId, null);
    }

    public void setWatchlist(int movieId, boolean watchlist) {
        db = DataBaseConnection.getInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues(1);
        cv.put(ContractClass.DBEntry.C_WATCHLIST, watchlist);
        db.update(ContractClass.DBEntry.T_MOVIE, cv, ContractClass.DBEntry._ID + " = " + movieId, null);
    }

    public Movie selectMovie(int movieID) {
        db = DataBaseConnection.getInstance(context).getReadableDatabase();
        String[] args = {String.valueOf(movieID)};
        Cursor c = db.rawQuery(ContractClass.DBQuery.QUERY_MOVIE, args);
        c.getCount();
        if (c.moveToFirst()) {
            Cursor cursorGenres = db.rawQuery(ContractClass.DBQuery.QUERY_GENRES, args);
            HashMap<Integer, String> genres = new HashMap<>();
            while (cursorGenres.moveToNext()) {
                genres.put(cursorGenres.getInt(0), cursorGenres.getString(1));
            }
            cursorGenres.close();
            Movie m = new Movie(c.getInt(0), c.getString(1), c.getString(2), c.getFloat(3),
                    c.getString(4), c.getString(5), c.getInt(6), c.getFloat(7), genres,
                    c.getString(8), c.getString(9), c.getString(10), c.getString(11), c.getString(12),
                    c.getInt(13) != 0, c.getInt(14) != 0, c.getInt(15) != 0);
            c.close();
            m.addAsInMemory();
            return m;
        }
        return null;
    }

    public boolean deleteMovie(int movieID) {
        db = DataBaseConnection.getInstance(context).getWritableDatabase();
        db.beginTransaction();
        db.delete(ContractClass.DBEntry.T_MOVIES_GENRES, ContractClass.DBEntry.C_FK_MOVIE + " = " +
                movieID, null);
        int r = db.delete(ContractClass.DBEntry.T_MOVIE, ContractClass.DBEntry._ID + " = " +
                movieID, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        return r > 0;
    }

    public List<Movie> selectList(Lists l) {
        db = DataBaseConnection.getInstance(context).getReadableDatabase();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String[] args;
        Cursor c;
        switch (l) {
            case WATCHED:
                c = db.rawQuery(ContractClass.DBQuery.QUERY_WATCHED, null);
                break;
            case WATCHLIST:
                c = db.rawQuery(ContractClass.DBQuery.QUERY_WATCHLIST, null);
                break;
            case POPULAR:
                c = db.rawQuery(ContractClass.DBQuery.QUERY_POPULAR, null);
                break;
            case UPCOMING:
                args = new String[2];
                calendar.add(Calendar.DATE, 1);
                args[0] = sdf.format(calendar.getTime());
                calendar.add(Calendar.DATE, 6);
                args[1] = sdf.format(calendar.getTime());
                c = db.rawQuery(ContractClass.DBQuery.QUERY_UPCOMING, args);
                break;
            case LATEST:
                args = new String[2];
                args[1] = sdf.format(calendar.getTime());
                calendar.add(Calendar.DATE, -6);
                args[0] = sdf.format(calendar.getTime());
                c = db.rawQuery(ContractClass.DBQuery.QUERY_LATEST, args);
                break;
            default:
                throw new RuntimeException("DbCrud.selectList: Undefined list query");
        }
        List<Movie> results = null;
        if (c.moveToNext()) {
            results = new ArrayList<>(20);
            do {
                int id = c.getInt(0);
                Movie m = Movie.getMovie(id);
                if (m == null) {
                    m = selectMovie(id);
                }
                results.add(m);
            } while (c.moveToNext());
        }
        c.close();
        return results;
    }

    public enum Lists {
        WATCHED,
        WATCHLIST,
        POPULAR,
        UPCOMING,
        LATEST
    }
}
