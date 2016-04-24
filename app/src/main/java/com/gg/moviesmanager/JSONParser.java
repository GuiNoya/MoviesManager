package com.gg.moviesmanager;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
    JSONObject reader;
    JSONArray jsonArray;

    public JSONParser(String jsonString) {
        try{
            reader = new JSONObject(jsonString);

            reader.getString("original_title");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
