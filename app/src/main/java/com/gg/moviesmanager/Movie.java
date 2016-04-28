package com.gg.moviesmanager;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Movie implements Parcelable {
    private int id;
    private String title;
    private String releaseDate;
    private float rating;
    private String overview;
    private String language;
    private int runtime;
    private float popularity;
    private Map<Integer, String> genres;
    private String cast;
    private String director;
    private String trailer = "";
    private String poster;
    private String backdrop;
    private boolean watchlist;
    private boolean watched;
    private boolean loaded;

    public Movie() {}

    public Movie(String title, float rating, String releaseDate){
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
    }

    public Movie(int id, String title, String releaseDate, float rating, String overview, String language,
                 int runtime, float popularity, Map<Integer, String> genres, String cast, String director,
                 String trailer, String poster, String backdrop, boolean watchlist, boolean watched,
                 boolean loaded) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.overview = overview;
        this.language = language;
        this.runtime = runtime;
        this.popularity = popularity;
        this.genres = genres;
        this.cast = cast;
        this.director = director;
        this.trailer = trailer;
        this.poster = poster;
        this.backdrop = backdrop;
        this.watchlist = watchlist;
        this.watched = watched;
        this.loaded = loaded;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        releaseDate = in.readString();
        rating = in.readFloat();
        overview = in.readString();
        language = in.readString();
        runtime = in.readInt();
        popularity = in.readFloat();
        cast = in.readString();
        director = in.readString();
        trailer = in.readString();
        poster = in.readString();
        backdrop = in.readString();
        watchlist = in.readByte() != 0;
        watched = in.readByte() != 0;
        loaded = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeFloat(rating);
        dest.writeString(overview);
        dest.writeString(language);
        dest.writeInt(runtime);
        dest.writeFloat(popularity);
        dest.writeString(cast);
        dest.writeString(director);
        dest.writeString(trailer);
        dest.writeString(poster);
        dest.writeString(backdrop);
        dest.writeByte((byte) (watchlist ? 1 : 0));
        dest.writeByte((byte) (watched ? 1 : 0));
        dest.writeByte((byte) (loaded ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getReleaseDate() {return releaseDate;}

    public void setReleaseDate(String releaseDate) {this.releaseDate = releaseDate;}

    public float getRating() {return rating;}

    public void setRating(float rating) {this.rating = rating;}

    public String getOverview() {return overview;}

    public void setOverview(String overview) {this.overview = overview;}

    public String getLanguage() {return language;}

    public void setLanguage(String language) {this.language = language;}

    public float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    public Map<Integer, String> getGenres() {return genres;}

    public void setGenres(Map<Integer, String> genres) {this.genres = genres;}

    public int getRuntime() {return runtime;}

    public void setRuntime(int runtime) {this.runtime = runtime;}

    public String getCast() {return cast;}

    public void setCast(String cast) {this.cast = cast;}

    public String getDirector() {return director;}

    public void setDirector(String director) {this.director = director;}

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

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
