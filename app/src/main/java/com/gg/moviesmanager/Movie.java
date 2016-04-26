package com.gg.moviesmanager;

import android.util.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Movie implements Serializable { //TODO: change to Parcelable
    private int id;
    private String title;
    private String releaseDate;
    private float grade;
    private String overview;
    private String language;
    private String rating;
    private int runtime;
    private Map<Integer, String> genres;
    private List<String> cast;
    private List<String> director;
    private String trailer;
    private String poster;
    private String backdrop;
    private boolean watchlist;
    private boolean watched;

    public Movie() {}

    public Movie(String title, float grade, String releaseDate){
        this.title = title;
        this.releaseDate = releaseDate;
        this.grade = grade;
    }

    public Movie(int id, String title, String releaseDate, float grade, String overview, String language, String rating,
                 int runtime, Map<Integer, String> genres, List<String> cast, List<String> director,
                 String trailer, String poster, String backdrop, boolean watchlist, boolean watched) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.grade = grade;
        this.overview = overview;
        this.language = language;
        this.rating = rating;
        this.runtime = runtime;
        this.genres = genres;
        this.cast = cast;
        this.director = director;
        this.trailer = trailer;
        this.poster = poster;
        this.backdrop = backdrop;
        this.watchlist = watchlist;
        this.watched = watched;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getReleaseDate() {return releaseDate;}

    public void setReleaseDate(String releaseDate) {this.releaseDate = releaseDate;}

    public float getGrade() {return grade;}

    public void setGrade(float grade) {this.grade = grade;}

    public String getOverview() {return overview;}

    public void setOverview(String overview) {this.overview = overview;}

    public String getLanguage() {return language;}

    public void setLanguage(String language) {this.language = language;}

    public String getRating() {return rating;}

    public void setRating(String rating) {this.rating = rating;}

    public Map<Integer, String> getGenres() {return genres;}

    public void setGenres(Map<Integer, String> genres) {this.genres = genres;}

    public int getRuntime() {return runtime;}

    public void setRuntime(int runtime) {this.runtime = runtime;}

    public List<String> getCast() {return cast;}

    public void setCast(List<String> cast) {this.cast = cast;}

    public List<String> getDirector() {return director;}

    public void setDirector(List<String> director) {this.director = director;}

    public boolean isWatchlist() {return watchlist;}

    public void setWatchlist(boolean watchlist) {this.watchlist = watchlist;}

    public boolean isWatched() {return watched;}

    public void setWatched(boolean watched) {this.watched = watched;}

    public String getTrailer() {return trailer;}

    public void setTrailer(String trailer) {this.trailer = trailer;}

    public String getPoster() {return poster;}

    public void setPoster(String poster) {this.poster = poster;}

    public String getBackdrop() {return backdrop;}

    public void setBackdrop(String backdrop) {this.backdrop = backdrop;}
}
