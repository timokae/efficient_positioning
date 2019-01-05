package com.example.team919.efficient_positioning;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.util.Log;
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
    Location collectedLocation;

    long minTime = 0;
    double minDistance = 0.0;
    double meterProSekunde = 0.0;

    EditText editName;
    EditText editDistanz;
    EditText editMinZeit;
    EditText editMS;
    RadioGroup radioGroup;
    RadioButton radioDistanz;
    RadioButton radioMinZeit;
    RadioButton radioMS;
    RadioButton radioStill;

    SensorManager sensorManager;
    float accelX = 0;
    float accelY = 0;
    float accelZ = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
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


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                        //editName.setText("X: " + String.valueOf(event.values[0]) + " Y: " + String.valueOf(event.values[1]) +" Z: " + String.valueOf(event.values[2]));
                        Log.d("gdsfzau", "X: " + String.valueOf(event.values[0]));
                        Log.d("gdsfzau", "Y: " + String.valueOf(event.values[1]));
                        Log.d("gdsfzau", "Z: " + String.valueOf(event.values[2]));
                        accelX = event.values[0];
                        accelY = event.values[1];
                        accelZ = event.values[2];

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED),SensorManager.SENSOR_DELAY_NORMAL);

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
                minDistance = Double.parseDouble(editDistanz.getText().toString());
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
                minTime = Long.parseLong(editMinZeit.getText().toString());
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
                meterProSekunde = Double.parseDouble(editMS.getText().toString());
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioMinZeit:
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                        setLocationUpdatesTime(minTime);
                        break;
                    case R.id.radioDistanz:
                        Toast.makeText(context, "Hallo Distanz", Toast.LENGTH_SHORT).show();
                        setLocationUpdatesTime(0);
                        break;

                    case R.id.radioMS:
                        minTime = (long)(minDistance/meterProSekunde);
                        setLocationUpdatesTime(minTime*1000);
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

                //1.a periodically
                if (radioMinZeit.isChecked()) doHttpRequest(location.getLongitude(), location.getLatitude(), location.getTime(), 0, editName.getText().toString());


                //1.b distance_based
                if (collectedLocation == null && radioDistanz.isChecked()){
                    collectedLocation = location;
                    doHttpRequest(collectedLocation.getLongitude(), collectedLocation.getLatitude(), collectedLocation.getTime(), 1, editName.getText().toString());
                }
                if (radioDistanz.isChecked() && distanceBetweenPoints(collectedLocation.getLatitude(), collectedLocation.getLongitude(), location.getLatitude(), location.getLongitude()) > minDistance){
                    //Log.d("gdsfzau", "next");
                    collectedLocation = location;
                    doHttpRequest(collectedLocation.getLongitude(), collectedLocation.getLatitude(), collectedLocation.getTime(), 1, editName.getText().toString());
                }

                //1.c geschqwindigkeit
                if (radioMS.isChecked())doHttpRequest(location.getLongitude(), location.getLatitude(), location.getTime(), 2, editName.getText().toString());


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

    public void doHttpRequest(double longitude, double latitude, long measured_at, int strategy, String name){
        try {
            JSONObject location = new JSONObject();
            location.put("longitude", longitude);
            location.put("latitude", latitude);
            location.put("measured_at", measured_at);
            location.put("strategy", strategy);
            location.put("name", name);

            JSONObject body = new JSONObject();
            body.put("location", location);

            new PushLocation().execute(body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public double distanceBetweenPoints(double startlat,double startlong,double entlat,double entlng) {
        Location locA = new Location("Point A");
        locA.setLatitude(startlat);
        locA.setLongitude(startlong);
        Location locB = new Location("Point B");
        locB.setLatitude(entlat);
        locB.setLongitude(entlng);

        double distance = (double) Math.round(locA.distanceTo(locB));
        return distance;
    }

}
