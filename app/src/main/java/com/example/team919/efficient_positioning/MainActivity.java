package com.example.team919.efficient_positioning;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;




import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    LocationListener locationListener;
    private LocationManager locationManager;
    private String locationProvider;
    final Context context = this;

    long minTime = 0;
    float minDistance = 0;
    double meterProSekunde = 0.0;

    EditText editDistanz;
    EditText editMinZeit;
    EditText editMS;
    RadioGroup radioGroup;
    RadioButton radioDistanz;
    RadioButton radioMinZeit;
    RadioButton radioMS;
    RadioButton radioStill;

    HttpRequest httpRequest = new HttpRequest();
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editDistanz  = findViewById(R.id.editDistanz);
        editMinZeit  = findViewById(R.id.editMinZeit);
        editMS =findViewById(R.id.editMS);
        radioGroup = findViewById(R.id.radioGroup);
        radioMinZeit = findViewById(R.id.radioMinZeit);
        radioDistanz = findViewById(R.id.radioDistanz);
        radioMS = findViewById(R.id.radioMS);
        radioStill =findViewById(R.id.radioStill);

        if (!fine_location_permitted()) {
            permit_fine_location(MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = getLocation();
        locationProvider = LocationManager.GPS_PROVIDER;

        //Distanz einstellen ---------------------------------------------------------------------
        editDistanz.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                minDistance = Float.parseFloat(editDistanz.getText().toString());
            }
        });


        //minimale Zeit einstellen ---------------------------------------------------------------------
        editMinZeit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                minDistance = Float.parseFloat(editMinZeit.getText().toString());
            }
        });

        //Meter pro Sekunde einstellen ---------------------------------------------------------------------
        editMS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                minDistance = Float.parseFloat(editMS.getText().toString());
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioDistanz:
                        Toast.makeText(context, "Hallo Distanz", Toast.LENGTH_SHORT).show();
                        doHttpRequest(55.213321, 77.2432, 23424, 0, "testtesttestxd");
                        break;

                    case R.id.radioMinZeit:
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();

                        break;

                    case R.id.radioMS:

                        Toast.makeText(context, editMinZeit.getText(), Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.radioStill:
                        break;

                }
            }
        });

    }




    void setLocationUpdatesTime(long minTime){
        locationManager.removeUpdates(locationListener);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(locationProvider,minTime,0,locationListener);
    }

    private LocationListener getLocation(){
        // Hier die Anwednung Starten Später noch aus Lagern
        //Toast.makeText(getApplicationContext(),"Berechtigung erteilt",Toast.LENGTH_LONG).show();
        // LocationListener erstellen

        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }


        };

    }

    // Fine Location Permission
    private boolean fine_location_permitted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void permit_fine_location(int callback) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, callback);
    }

    private void doHttpRequest(double latitude, double longitude, int measured_at, int strategy, String name){
        try {
            jsonObject.put("latitude",  latitude);
            jsonObject.put("longitude",  longitude);
            jsonObject.put("measured_at",  measured_at);
            jsonObject.put("strategy",  strategy);
            jsonObject.put("name",  name);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(jsonObject);
        httpRequest.doInBackground(jsonArray.toString());
    }
}
