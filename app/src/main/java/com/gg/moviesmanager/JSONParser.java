package com.gg.moviesmanager;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONParser extends AsyncTask<String, Void, Void> {
    private String jsonString;
    private boolean isMovie;
    private AsyncAccessResultMovieJson asyncAccessResultMovieJson = null;
    private AsyncAccessResultListJson asyncAccessResultListJson = null;

    private Movie movie;
    private List<Movie> results;

    public JSONParser(String jsonString, AsyncAccessResultMovieJson asyncAccessResultMovieJson) {
        this.jsonString = jsonString;
        this.isMovie = true;
        this.asyncAccessResultMovieJson = asyncAccessResultMovieJson;
    }

    public JSONParser(String jsonString, AsyncAccessResultListJson asyncAccessResultListJson) {
        this.jsonString = jsonString;
        this.isMovie = false;
        this.asyncAccessResultListJson = asyncAccessResultListJson;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            JSONObject root = new JSONObject(jsonString);
            if (isMovie) {
                movie = generateMovie(root, true);
            } else {
                results = new ArrayList<>(20);
                JSONArray jsonArray = root.getJSONArray("results");
                int i = 0; int l = jsonArray.length();
                for (JSONObject o = jsonArray.optJSONObject(i); i < l; i++) {
                    results.add(generateMovie(o, false));
                }
            }
        } catch (JSONException e) {
            Log.e("JSONParser", "Problem reading JSON string", e);
        }
        return null;
    }

    private Movie generateMovie(JSONObject obj, boolean complete) {
        Movie movie = new Movie();
        try {
            movie.setId(obj.getInt("id"));
            movie.setTitle(obj.getString("title"));
            movie.setReleaseDate(obj.optString("release_date"));
            movie.setRating((float) obj.optDouble("vote_average"));
            movie.setOverview(obj.getString("overview"));
            movie.setLanguage(obj.optString("original_language"));
            movie.setPopularity((float) obj.optDouble("popularity"));
            movie.setPoster(obj.optString("poster_path", "/").substring(1));
            movie.setBackdrop(obj.optString("backdrop_path", "/").substring(1));

            JSONArray jsonArray = obj.getJSONArray("genres");
            HashMap<Integer, String> genres = new HashMap<>();
            int i = 0;
            int l = jsonArray.length();
            for (JSONObject o = jsonArray.optJSONObject(i); i < l; i++) {
                genres.put(o.getInt("id"), o.getString("name"));
            }
            movie.setGenres(genres);

            movie.setWatchlist(false);
            movie.setWatched(false);

            if (complete) {
                movie.setLoaded(true);
                movie.setRuntime(obj.optInt("runtime"));

                JSONObject credits = obj.getJSONObject("credits");
                jsonArray = credits.getJSONArray("crew");
                i = 0;
                l = jsonArray.length();
                for (JSONObject o = jsonArray.optJSONObject(i); i < l; i++) {
                    if ("Director".equals(o.optString("job"))) {
                        movie.setDirector(o.optString("name"));
                        break;
                    }
                }

                jsonArray = credits.getJSONArray("cast");
                i = 0;
                l = jsonArray.length();
                String cast = "";
                for (JSONObject o = jsonArray.optJSONObject(i); i < l || i < 5; i++) {
                    cast += o.getString("name") + ", ";
                }
                if (cast.length() > 0) {
                    movie.setCast(cast.substring(cast.length() - 2, cast.length()));
                } else {
                    movie.setCast(cast);
                }

                jsonArray = obj.getJSONObject("videos").getJSONArray("results");
                i = 0;
                l = jsonArray.length();
                for (JSONObject o = jsonArray.optJSONObject(i); i < l; i++) {
                    if ("Trailer".equals(o.getString("type"))) {
                        movie.setTrailer("http://www.youtube.com/watch?v=" + o.getString("key"));
                        break;
                    }
                }
            } else {
                movie.setLoaded(false);
            }
        } catch (JSONException e) {
            Log.e("JSONParser", "Could not load movie.", e);
            return null;
        }
        return movie;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (asyncAccessResultMovieJson != null) {
            asyncAccessResultMovieJson.accessResult(movie);
        } else if (asyncAccessResultListJson != null) {
            asyncAccessResultListJson.accessResult(results);
        }
    }

    public interface AsyncAccessResultMovieJson {
        void accessResult(Movie asyncResult);
    }

    public interface AsyncAccessResultListJson {
        void accessResult(List<Movie> asyncResult);
    }
}
