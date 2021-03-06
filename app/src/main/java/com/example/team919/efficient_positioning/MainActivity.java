package com.example.team919.efficient_positioning;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    LocationListener locationListener;
    private LocationManager locationManager;
    private String locationProvider;
    final Context context = this;
    Location collectedLocation;

    int gpsFixes = 0;

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

    Button btnStart;
    Button btnStop;
    TextView txtLat;
    TextView txtLong;

    SensorManager sensorManager;
    boolean flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        txtLat = findViewById(R.id.txtLat);
        txtLong = findViewById(R.id.txtLong);


        if (!fine_location_permitted()) {
            permit_fine_location();
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = getLocation();
        locationProvider = LocationManager.GPS_PROVIDER;


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER:

                        if (radioStill.isChecked() && Math.abs(event.values[0]) < 1 && Math.abs(event.values[1]) < 1 && Math.abs(event.values[2]) > 8 && flag){
                            locationManager.removeUpdates(locationListener);
                            flag = false;
                        }else if(radioStill.isChecked() && Math.abs(event.values[0]) > 1 && Math.abs(event.values[1]) > 1 && Math.abs(event.values[2]) < 8 && !flag){
                            setLocationUpdatesTime(minTime*1000);
                            flag = true;
                        }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);

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
                if(editDistanz.getText().toString().trim().length()!=0) minDistance = Double.parseDouble(editDistanz.getText().toString());
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
                if(editMinZeit.getText().toString().trim().length()!=0) minTime = Long.parseLong(editMinZeit.getText().toString());
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
                if(editMS.getText().toString().trim().length()!=0) meterProSekunde = Double.parseDouble(editMS.getText().toString());
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioMinZeit:
                        editDistanz.setEnabled(false);
                        editMS.setEnabled(false);
                        editMinZeit.setEnabled(true);
                        break;
                    case R.id.radioDistanz:
                        editDistanz.setEnabled(true);
                        editMS.setEnabled(false);
                        editMinZeit.setEnabled(false);
                        break;

                    case R.id.radioMS:
                        editDistanz.setEnabled(true);
                        editMS.setEnabled(true);
                        editMinZeit.setEnabled(false);
                        break;

                    case R.id.radioStill:
                        editDistanz.setEnabled(true);
                        editMS.setEnabled(true);
                        editMinZeit.setEnabled(false);
                        break;
                }
            }
        });

        btnStart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioMinZeit.isChecked()){
                    setLocationUpdatesTime(minTime*1000);
                }
                if(radioDistanz.isChecked()){
                    setLocationUpdatesTime(0);
                }
                if(radioMS.isChecked()){
                    minTime = (long)(minDistance/meterProSekunde);
                    setLocationUpdatesTime(minTime*1000);
                }
                if(radioStill.isChecked()){
                    minTime = (long)(minDistance/meterProSekunde);
                    setLocationUpdatesTime(minTime*1000);
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager.removeUpdates(locationListener);
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
        // LocationListener erstellen

        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                txtLat.setText(String.valueOf(location.getLatitude()));
                txtLong.setText(String.valueOf(location.getLongitude()));

                //1.a periodically
                if (radioMinZeit.isChecked()) doHttpRequest(location.getLongitude(), location.getLatitude(), location.getTime(), 0, editName.getText().toString());


                //1.b distance_based
                gpsFixes++;
                Log.d("GPS_Fixes", String.valueOf(gpsFixes));
                if (collectedLocation == null && radioDistanz.isChecked()){
                    collectedLocation = location;
                    doHttpRequest(collectedLocation.getLongitude(), collectedLocation.getLatitude(), collectedLocation.getTime(), 1, editName.getText().toString());
                }
                if (radioDistanz.isChecked() && distanceBetweenPoints(collectedLocation.getLatitude(), collectedLocation.getLongitude(), location.getLatitude(), location.getLongitude()) > minDistance){
                    collectedLocation = location;
                    doHttpRequest(collectedLocation.getLongitude(), collectedLocation.getLatitude(), collectedLocation.getTime(), 1, editName.getText().toString());
                }

                //1.c speed_based
                if (radioMS.isChecked())doHttpRequest(location.getLongitude(), location.getLatitude(), location.getTime(), 2, editName.getText().toString());

                //1.d sensor_based
                if (radioStill.isChecked())doHttpRequest(location.getLongitude(), location.getLatitude(), location.getTime(), 3, editName.getText().toString());
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

    private void permit_fine_location() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MainActivity.MY_PERMISSIONS_REQUEST_FINE_LOCATION);
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

    public double distanceBetweenPoints(double startLat,double startLong,double entLat,double entLng) {
        Location locA = new Location("Point A");
        locA.setLatitude(startLat);
        locA.setLongitude(startLong);
        Location locB = new Location("Point B");
        locB.setLatitude(entLat);
        locB.setLongitude(entLng);

        return (double) Math.round(locA.distanceTo(locB));
    }

}
