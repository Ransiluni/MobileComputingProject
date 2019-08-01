package com.app2;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SensorDetect extends Service implements SensorEventListener , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private RequestQueue queue ;
    private SensorManager sm;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private ArrayList<LatLng> restaurantLocations =new ArrayList<LatLng>();
    private LatLng currentRestaurant;
    private Sensor light;
    private Sensor sound;
    private Sensor temp;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private MediaRecorder recorder = null;
    private double amplitudeValue;
    private long lastTime=Calendar.getInstance().getTimeInMillis();
    private LocationManager locationManager;
    private ArrayList<Float> sensor_Data=new ArrayList<Float>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getBaseContext(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }
        queue= Volley.newRequestQueue( getBaseContext());
        sm=(SensorManager) getSystemService(SENSOR_SERVICE);
//        List<Sensor> msensorList = sm.getSensorList(Sensor.TYPE_ALL);
//
//        // Print how may Sensors are there
//
//
//        // Print each Sensor available using sSensList as the String to be printed
//        String sSensList = "";
//        Sensor tmp;
//        int x,i;
//        for (i=0;i<msensorList.size();i++){
//            tmp = msensorList.get(i);
//            sSensList = " "+sSensList+tmp.getName(); // Add the sensor name to the string of sensors available
//        }
//        Log.i("App",sSensList);
//        Toast.makeText(this,sSensList,Toast.LENGTH_LONG).show();
        light=sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        //light=sm.getDefaultSensor(Sensor.TYPE_);

        receiveRestaurantData();

       // restaurantLocations.add(new LatLng(0.11,80.7700));  // add restaurant locations through HTTP call
}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager)this.getSystemService(this.LOCATION_SERVICE);

        if (checkLocation()&& mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        //sm.registerListener(this,s, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient != null){
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        sm.unregisterListener(this);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        sensor_Data.add(values[0]);
        String value="Sensor Value : " + values[0];

        sendSensorData();
    }

    private void sendSensorData(){
        long diff=Calendar.getInstance().getTimeInMillis()-lastTime;
        Log.d("AAAAAAAAAAAAAAA", lastTime+"   "+Calendar.getInstance().getTimeInMillis());
        if(diff>20000){
            String url = "https://mobilecomputingproject.herokuapp.com/data/set";
            Map<String, String>  params = new HashMap<String, String>();
            Toast.makeText(this, String.valueOf(calculateAverage(sensor_Data)), Toast.LENGTH_SHORT).show();
            params.put("lat", String.valueOf(currentRestaurant.latitude));
            params.put("long", String.valueOf(currentRestaurant.longitude));
            params.put("light_data", String.valueOf(calculateAverage(sensor_Data)));
            params.put("noise_data", String.valueOf(amplitudeValue));
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(params),
                    response -> {
                        // response
                        Log.d("Response", response.toString());
                    }, error -> {
                // error
                Log.d("Error.Response", String.valueOf(error));
            }
            );
            lastTime=Calendar.getInstance().getTimeInMillis();
            queue.add(postRequest);
        }

    }

    private void receiveRestaurantData(){
        String url = "https://mobilecomputingproject.herokuapp.com/place/list";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                response -> {
                    // response
                    try {
                        JSONArray restaurantList= (JSONArray) response.get("items");
                        for (int i=0;i<restaurantList.length();i++){
                            JSONObject restaurant=restaurantList.getJSONObject(i);
                            restaurantLocations.add(new LatLng(new Double(restaurant.get("lat").toString()),new Double(restaurant.get("long").toString())));
                        }
                        Log.d("array",restaurantList.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            // error
            Log.d("Error.Response", String.valueOf(error));
        }
        );
        queue.add(getRequest);

    }

    private double calculateAverage(ArrayList<Float> marks) {
        float sum = 0;
        if(!marks.isEmpty()) {
            for (float mark : marks) {
                sum += mark;
            }
            return sum/ marks.size();
        }
        return sum;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        AlertDialog dialog1=dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.Please Enable Location to use this App")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                }).create();
        dialog1.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog1.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
        Log.d("MyApp","hi "+ "Connected");
        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(this, "Location not Detected. Turn your WIFI or data connection ON", Toast.LENGTH_SHORT).show();
        }

    }

    protected void startLocationUpdates() {
        // Create the location request
        Toast.makeText(this, "Location Updates", Toast.LENGTH_SHORT).show();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("MyApp", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                location.getLatitude() + "," +
                location.getLongitude();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng sLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(checkRange(sLatLng)){
            sm.registerListener(this,light, SensorManager.SENSOR_DELAY_NORMAL);
            startRecording();
        }else{
            sm.unregisterListener(this);
            sensor_Data.clear();
        }
    }

    protected boolean checkRange(LatLng sLatLng){

        for(LatLng dLatLng:restaurantLocations){
            if(Math.acos(Math.sin(sLatLng.latitude) * Math.sin(dLatLng.latitude) + Math.cos(sLatLng.latitude) *
                    Math.cos(dLatLng.latitude) * Math.cos(dLatLng.longitude - (sLatLng.longitude))) * 6371 <= 0.8)
            {
                currentRestaurant=dLatLng;
                return true;
            }
        }
        return false;

    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile("/dev/null");
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
            recorder.getMaxAmplitude();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if(recorder!=null){
                int amplitude = recorder.getMaxAmplitude();
                amplitudeValue = 20 * Math.log10(amplitude / 0.2);
                Log.d("SSSSSSS",amplitudeValue+"  " + amplitude);
                stopRecording();}
            }, 10000);

        } catch (Exception e) {
            Log.i("App", e.toString());
        }

    }

    private void stopRecording() {
        try{
            recorder.stop();
            recorder.release();
        }catch(RuntimeException stopException){
            //handle cleanup here
        }

        recorder = null;
    }
}