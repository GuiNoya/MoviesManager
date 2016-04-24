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

    public AsyncAccessResult resultAccess = null;

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
