package com.example.team919.efficient_positioning;

import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;


public class HttpRequest extends AsyncTask<String, Integer, Long> {


    @Override
    protected Long doInBackground(String... params) {
        String urlstring = "https://lit-hollows-83879.herokuapp.com/locations";
        String requestBody = params[0];

        try{
            url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");

        }
        return null;
    }
}
