package com.gg.moviesmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataDownloader {

    private static final String urlBase = "http://api.themoviedb.org/3/";
    private static final String urlAPI = "?api_key=" + BuildConfig.TMDb_API_KEY;
    private static final String urlImagePoster = "http://image.tmdb.org/t/p/w92/";
    private static final String urlImageBack = "http://image.tmdb.org/t/p/w300/";

    private DataDownloader() { }

    public static String getMovie(int id){
        String urlComplete = urlBase + "movie/" + id + urlAPI + "&append_to_response=videos,credits";
        return download(urlComplete);
    }

    public void getMovieImage(boolean poster, String fileName){
        String urlComplete;
        if (poster)
            urlComplete = urlImagePoster + fileName;
        else
            urlComplete = urlImageBack + fileName;
        download(urlComplete);
    }

    public static String getLatest(int page){
        String urlComplete = urlBase + "movie/now_playing" + urlAPI;
        return download(urlComplete);
    }

    public static String getPopulars(int page){
        String urlComplete = urlBase + "movie/popular" + urlAPI;
        return download(urlComplete);
    }

    public static String getUpcoming(int page){
        String urlComplete = urlBase + "movie/upcoming" + urlAPI;
        return download(urlComplete);
    }

    private static String download(String address) {
        String result = "";
        try {
            URL url = new URL(address);
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
