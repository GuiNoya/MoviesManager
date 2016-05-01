package com.gg.moviesmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Class responsible for stabilising the connection with the internet and downloading the data.
 * It creates the complete URL for the necessary request.
 */
public class DataDownloader {

    // TMDb_API_KEY needs to be placed in a gradle.properties file (sensitive data).
    private static final String urlAPI = "?api_key=" + BuildConfig.TMDb_API_KEY;
    // Basic URL formats to access the API.
    private static final String urlBase = "http://api.themoviedb.org/3/";
    private static final String urlImagePoster = "http://image.tmdb.org/t/p/w92/";
    private static final String urlImageBack = "http://image.tmdb.org/t/p/w300/";

    private DataDownloader() { }

    public static String getSearch(String query) {
        String urlComplete = urlBase + "search/movie" + urlAPI + "&query=";
        try {
            urlComplete += URLEncoder.encode(query, "UTF-8"); // CGI-escaped query
        } catch (UnsupportedEncodingException e) {
            return "";
        }
        return download(urlComplete);
    }

    public static String getMovie(int id){
        String urlComplete = urlBase + "movie/" + id + urlAPI + "&append_to_response=videos,credits";
        return download(urlComplete);
    }

    public static String getLatest(){
        String urlComplete = urlBase + "movie/now_playing" + urlAPI;
        return download(urlComplete);
    }

    public static String getPopulars(){
        String urlComplete = urlBase + "movie/popular" + urlAPI;
        return download(urlComplete);
    }

    public static String getUpcoming(){
        String urlComplete = urlBase + "movie/upcoming" + urlAPI;
        return download(urlComplete);
    }

    public static String getVideos(int id) {
        String urlComplete = urlBase + "movie/" + id + "/videos" + urlAPI;
        return download(urlComplete);
    }

    /**
     * Stabilises a connection, gets the InputStream,
     * goes through the bytes passing them to a string.
     *
     * @param address The full URL to be fetched.
     * @return String with the result or empty string if a problem occurred.
     */
    private static String download(String address) {
        String result = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedInputStream input = new BufferedInputStream(connection.getInputStream());

            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int byteRead = input.read();
                while (byteRead != -1) {
                    bo.write(byteRead);
                    byteRead = input.read();
                }
                result = bo.toString();
            }
            catch (IOException e) {
                result = "";
                e.printStackTrace();
            }

            connection.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Specific method to download an image and save it to the internal storage.
     *
     * @param context Application context for the image to be saved in.
     * @param name Name of the image.
     * @param type Type of the image (A movie can have a poster and a backdrop).
     */
    public static void downloadImage(Context context, String name, TypeImage type) {
        try {
            File f = context.getFileStreamPath(name);
            if (!f.exists()) {
                URL url;
                if (type == TypeImage.POSTER) {
                    url = new URL(urlImagePoster + name);
                } else {
                    url = new URL(urlImageBack + name);
                }

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Bitmap img = BitmapFactory.decodeStream(connection.getInputStream());

                FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
                img.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                connection.disconnect();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum TypeImage {
        POSTER,
        BACKDROP
    }
}
