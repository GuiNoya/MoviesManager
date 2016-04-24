package com.gg.moviesmanager;

import java.io.Serializable;

public class Movie implements Serializable { //TODO: change to Parcelable
    private String name;
    private String releaseDate;
    private float rating;

    public Movie(String name, float rating, String releaseDate) {
        this.rating = rating;
        this.name = name;
        this.releaseDate = releaseDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
