package com.gg.moviesmanager;

import android.provider.BaseColumns;

public final class ContractClass {
    private ContractClass(){};

    public static abstract class DBEntry implements BaseColumns {
        public static final String T_MOVIE = "Movie";
        public static final String T_GENRE = "Genre";
        public static final String T_MOVIES_GENRES = "Movies_Genres";
        public static final String C_ID = "_id";

        public static final String C_TITLE = "title";
        public static final String C_RELEASE = "release_date";
        public static final String C_GRADE = "grade";
        public static final String C_OVERVIEW = "overview";
        public static final String C_LANGUAGE = "language";
        public static final String C_RATING = "rating";
        public static final String C_RUNTIME = "runtime";
        public static final String C_CAST = "cast";
        public static final String C_DIRECTOR = "director";
        public static final String C_TRAILER = "trailer";
        public static final String C_IMPOSTER = "poster";
        public static final String C_IMBACK = "backdrop";
        public static final String C_WATCHLIST = "watchlist";
        public static final String C_WATCHED = "watched";

        public static final String C_NAME = "name";

        public static final String C_FK_MOVIE = "movie_id";
        public static final String C_FK_GENRE = "genre_id";
    }

}
