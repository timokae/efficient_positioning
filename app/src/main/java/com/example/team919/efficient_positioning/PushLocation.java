package com.example.team919.efficient_positioning;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PushLocation extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String url = "https://lit-hollows-83879.herokuapp.com/api/locations/create";
        //String url = "http://192.168.0.45:3000/api/locations/create";
        String result = new String("");
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, params[0]);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            result = response.body().string();
        } catch(IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("Response", result);
    }
}
