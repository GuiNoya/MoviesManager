package com.gg.moviesmanager;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class JSONParser {

    public static String getTrailer(String jsonString) {
        try {
            JSONObject o = new JSONObject(jsonString);
            JSONArray results = o.getJSONArray("results");
            int i = 0;
            int l = results.length();
            while (i < l) {
                o = results.optJSONObject(i);
                if ("Trailer".equals(o.getString("type"))) {
                    return "http://www.youtube.com/watch?v=" + o.getString("key");
                }
                i++;
            }
            return null;
        } catch (JSONException e) {
            Log.e("JSONParser", "Could not get trailer");
            return null;
        }
    }

    public static ArrayList<Movie> parseList(String jsonString) {
        ArrayList<Movie> results;
        try {
            JSONObject root = new JSONObject(jsonString);
            results = new ArrayList<>(20);
            JSONArray jsonArray = root.getJSONArray("results");
            int i = 0; int l = jsonArray.length();
            JSONObject o;
            for (; i < l; i++) {
                o = jsonArray.optJSONObject(i);
                results.add(parseSingleMovie(o, false));
            }
        } catch (JSONException e) {
            Log.e("JSONParser", "Problem reading JSON string", e);
            results = null;
        }
        return results;
    }

    public static Movie parseMovie(String jsonString) {
        Movie movie;
        try {
            JSONObject root = new JSONObject(jsonString);
            movie = parseSingleMovie(root, true);
        } catch (JSONException e) {
            Log.e("JSONParser", "Problem reading JSON string", e);
            movie = null;
        }
        return movie;
    }

    private static Movie parseSingleMovie(JSONObject obj, boolean complete) {
        Movie movie = new Movie();
        try {
            movie.setId(obj.getInt("id"));
            movie.setTitle(obj.getString("title"));
            movie.setReleaseDate(obj.optString("release_date"));
            movie.setRating((float) obj.optDouble("vote_average"));
            movie.setOverview(obj.getString("overview"));
            movie.setLanguage(obj.optString("original_language"));
            movie.setPopularity((float) obj.optDouble("popularity"));
            String s = obj.optString("poster_path", "/");
            movie.setPoster(s.equals("null") ? "" : s.substring(1));
            s = obj.optString("backdrop_path", "/");
            movie.setBackdrop(s.equals("null") ? "" : s.substring(1));

            if (complete) {
                movie.setLoaded(true);
                movie.setRuntime(obj.optInt("runtime"));

                JSONArray jsonArray = obj.getJSONArray("genres");
                HashMap<Integer, String> genres = new HashMap<>();
                int i = 0;
                int l = jsonArray.length();
                JSONObject o;
                while (i < l) {
                    o = jsonArray.optJSONObject(i);
                    genres.put(o.getInt("id"), o.getString("name"));
                    i++;
                }
                movie.setGenres(genres);

                JSONObject credits = obj.getJSONObject("credits");
                jsonArray = credits.getJSONArray("crew");
                i = 0;
                l = jsonArray.length();
                while (i < l) {
                    o = jsonArray.optJSONObject(i);
                    if ("Director".equals(o.optString("job"))) {
                        movie.setDirector(o.optString("name"));
                        break;
                    }
                    i++;
                }

                jsonArray = credits.getJSONArray("cast");
                i = 0;
                l = jsonArray.length();
                String cast = "";
                while (i < l && i < 5) {
                    o = jsonArray.optJSONObject(i);
                    cast += o.getString("name") + ", ";
                    i++;
                }
                if (cast.length() > 0) {
                    movie.setCast(cast.substring(0, cast.length() - 3));
                } else {
                    movie.setCast(cast);
                }

                jsonArray = obj.getJSONObject("videos").getJSONArray("results");
                i = 0;
                l = jsonArray.length();
                while (i < l) {
                    o = jsonArray.optJSONObject(i);
                    if ("Trailer".equals(o.getString("type"))) {
                        movie.setTrailer("http://www.youtube.com/watch?v=" + o.getString("key"));
                        break;
                    }
                    i++;
                }
            } else {
                movie.setWatchlist(false);
                movie.setWatched(false);
                movie.setLoaded(false);
            }
        } catch (JSONException e) {
            Log.e("JSONParser", "Could not load movie.", e);
            return null;
        }
        return movie;
    }
}
