package com.gg.moviesmanager;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class JSONParser extends AsyncTask<String, Void, Void> {
    private String jsonString;
    private Types type;

    private Movie movie;

    public JSONParser(Types type, String jsonString) {
        this.type = type;
        this.jsonString = jsonString;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            JSONObject root = new JSONObject(jsonString);
            if (type == Types.Movie) {
                movie = new Movie();
                movie.setId(root.getInt("id"));
                movie.setTitle(root.getString("title"));
                movie.setReleaseDate(root.getString("release_date"));
                movie.setRating((float) root.getDouble("vote_average"));
                movie.setOverview(root.getString("overview"));
                movie.setLanguage(root.getString("original_language"));
                movie.setRuntime(root.getInt("runtime"));
                movie.setPoster(root.getString("poster_path"));
                movie.setBackdrop(root.getString("backdrop_path"));

                movie.setWatchlist(false);
                movie.setWatched(false);

                JSONArray jsonArray = root.getJSONArray("genres");
                HashMap<Integer, String> genres = new HashMap<>();
                int i = 0;
                for (JSONObject o = jsonArray.getJSONObject(i); i < jsonArray.length(); i++) {
                    genres.put(o.getInt("id"), o.getString("name"));
                }
                movie.setGenres(genres);

                JSONObject credits = root.getJSONObject("credits");
                jsonArray = credits.getJSONArray("crew");
                i = 0;
                for (JSONObject o = jsonArray.getJSONObject(i); i < jsonArray.length(); i++) {
                    if ("Director".equals(o.getString("job"))) {
                        movie.setDirector(o.getString("name"));
                        break;
                    }
                }

                jsonArray = credits.getJSONArray("cast");
                i = 0;
                StringBuilder cast = new StringBuilder();
                for (JSONObject o = jsonArray.getJSONObject(i); i < jsonArray.length() || i < 5; i++) {
                    cast.append(o.getString("name"));
                    cast.append(", ");
                }
                cast.delete(cast.length() - 2, cast.length());
                movie.setCast(cast.toString());

                jsonArray = root.getJSONObject("videos").getJSONArray("results");
                i = 0;
                for (JSONObject o = jsonArray.getJSONObject(i); i < jsonArray.length(); i++) {
                    if ("Trailer".equals(o.getString("type"))) {
                        movie.setTrailer("http://www.youtube.com/watch?v=" + o.getString("key"));
                        break;
                    }
                }
            } else {
                Log.i("JSONParser", "Not implemented yet!");
                //TODO: other cases
            }
        } catch (JSONException e) {
            Log.e("JSONParser", "Problem reading JSON string", e);
        }
        return null;
    }

    public enum Types {
        Movie,
        Upcoming,
        Popular,
        NowPlaying
    }
}
