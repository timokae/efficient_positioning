package com.example.team919.efficient_positioning;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpRequest extends AsyncTask<String, Integer, Long> {


    @Override
    protected Long doInBackground(String... params) {
        String urlstring = "https://lit-hollows-83879.herokuapp.com/locations";
        String requestBody = params[0];

        try{
            URL url;
            url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/json");

            OutputStream outputStream = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
            writer.flush();
            writer.close();
            outputStream.close();


            InputStream inputStream;
            if(conn.getResponseCode()<HttpURLConnection.HTTP_BAD_REQUEST){
                inputStream = conn.getInputStream();
            }else{
                inputStream = conn.getErrorStream();
            }
            conn.disconnect(); //trennung der verbinung

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Object temp;
            while((temp = bufferedReader.readLine()) != null){
                Log.d("Ergebnis: ", (String) temp);
            }


        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
