package com.gg.moviesmanager;

import android.provider.BaseColumns;

public final class ContractClass {
    private ContractClass(){}

    public static abstract class DBQuery {
        public static final String QUERY_WATCHED = "SELECT " + DBEntry.C_TITLE + ", " +
                DBEntry.C_RELEASE + ", " + DBEntry.C_RATING + ", " + DBEntry.C_IMPOSTER + " FROM " +
                DBEntry.T_MOVIE + " WHERE " + DBEntry.C_WATCHED + " = 1";
        public static final String QUERY_WATCHLIST = "SELECT " + DBEntry.C_TITLE + ", " +
                DBEntry.C_RELEASE + ", " + DBEntry.C_RATING + ", " + DBEntry.C_IMPOSTER + " FROM " +
                DBEntry.T_MOVIE + " WHERE " + DBEntry.C_WATCHLIST + " = 1";
        public static final String QUERY_POPULARS = "SELECT " + DBEntry.C_TITLE + ", " +
                DBEntry.C_RELEASE + ", " + DBEntry.C_RATING + ", " + DBEntry.C_IMPOSTER + " FROM " +
                DBEntry.T_MOVIE + " ORDER BY " + DBEntry.C_POPULARITY + " LIMIT 20";
        public static final String QUERY_UPCOMING = "SELECT " + DBEntry.C_TITLE + ", " +
                DBEntry.C_RELEASE + ", " + DBEntry.C_RATING + ", " + DBEntry.C_IMPOSTER + " FROM " +
                DBEntry.T_MOVIE +  " WHERE " + DBEntry.C_RELEASE + " BETWEEN ? AND ? ORDER BY " +
                DBEntry.C_RELEASE + " LIMIT 20";
        public static final String QUERY_LATEST = "SELECT " + DBEntry.C_TITLE + ", " +
                DBEntry.C_RELEASE + ", " + DBEntry.C_RATING + ", " + DBEntry.C_IMPOSTER + " FROM " +
                DBEntry.T_MOVIE +  " WHERE " + DBEntry.C_RELEASE + " BETWEEN ? AND ? ORDER BY " +
                DBEntry.C_RELEASE + " LIMIT 20";
        public static final String QUERY_MOVIE_EXISTS = "SELECT " + DBEntry.C_LOADED + " FROM " +
                DBEntry.T_MOVIE + " WHERE " + DBEntry._ID + " = ?";
        public static final String QUERY_MOVIE = "SELECT * FROM " + DBEntry.T_MOVIE + " WHERE " +
                DBEntry._ID + " = ?";
        public static final String QUERY_GENRES = "SELECT G." + DBEntry._ID + ", G." +
                DBEntry.C_NAME + " FROM " + DBEntry.T_GENRE + " G, " + DBEntry.T_MOVIES_GENRES +
                " MG WHERE MG." + DBEntry.C_FK_MOVIE + " = ? AND MG." + DBEntry.C_FK_GENRE +
                " = G." + DBEntry._ID;
    }

    public static abstract class DBEntry implements BaseColumns {
        public static final String T_MOVIE = "Movie";
        public static final String T_GENRE = "Genre";
        public static final String T_MOVIES_GENRES = "Movies_Genres";

        public static final String C_TITLE = "title";
        public static final String C_RELEASE = "release_date";
        public static final String C_RATING = "rating";
        public static final String C_OVERVIEW = "overview";
        public static final String C_LANGUAGE = "language";
        public static final String C_RUNTIME = "runtime";
        public static final String C_POPULARITY = "popularity";
        public static final String C_CAST = "cast";
        public static final String C_DIRECTOR = "director";
        public static final String C_TRAILER = "trailer";
        public static final String C_IMPOSTER = "poster";
        public static final String C_IMBACK = "backdrop";
        public static final String C_WATCHLIST = "watchlist";
        public static final String C_WATCHED = "watched";
        public static final String C_LOADED = "loaded";

        public static final String C_NAME = "name";

        public static final String C_FK_MOVIE = "movie_id";
        public static final String C_FK_GENRE = "genre_id";
    }

}
