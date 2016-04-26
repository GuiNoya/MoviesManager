package com.gg.moviesmanager;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataConnection extends AsyncTask<String, Integer, String> {

    public interface AsyncAccessResult {
        void accessResult(String asyncResult);
    }

    private AsyncAccessResult resultAccess;
    private static final String urlBase = "http://api.themoviedb.org/3/";
    private static final String urlAPI = "?api_key=" + BuildConfig.TMDb_API_KEY;
    private static final String urlImagePoster = "http://image.tmdb.org/p/w92/";
    private static final String urlImageBack = "http://image.tmdb.org/p/w300/";

    public DataConnection(AsyncAccessResult resultAccess) {
        this.resultAccess = resultAccess;
    }

    public void getMovie(int id){
        String urlComplete = urlBase + "movie/" + id + urlAPI + "&append_to_response=videos,credits";
        execute(urlComplete);
    }

    public void getMovieImage(boolean poster, String fileName){
        String urlComplete = "";
        if (poster)
            urlComplete = urlImagePoster + fileName;
        else
            urlComplete = urlImageBack + fileName;
        execute(urlComplete);
    }

    public void getNowPlaying(int page){
        String urlComplete = urlBase + "movie/now_playing" + urlAPI;
        execute(urlComplete);
    }

    public void getPopulars(int page){
        String urlComplete = urlBase + "movie/popular" + urlAPI;
        execute(urlComplete);
    }

    public void getUpcoming(int page){
        String urlComplete = urlBase + "movie/upcoming" + urlAPI;
        execute(urlComplete);
    }

    @Override
    protected String doInBackground(String... address) {
        String result = "";
        try {
            URL url = new URL(address[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedInputStream input = new BufferedInputStream(connection.getInputStream());

            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int byteRead = input.read();
                while (byteRead != -1) { // no more output
                    bo.write(byteRead);
                    byteRead = input.read();
                }
                result = bo.toString();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            connection.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        if (resultAccess != null)
            resultAccess.accessResult(s);
    }
}
