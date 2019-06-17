package com.app2;

import android.Manifest;
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
import android.os.Bundle;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.imagepipeline.common.SourceUriType;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SensorDetect extends Service implements SensorEventListener , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private RequestQueue queue = Volley.newRequestQueue(this);
    private SensorManager sm;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private ArrayList<LatLng> restaurantLocations;
    private int currentRestaurant;
    private Sensor s;

    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sm=(SensorManager) getSystemService(SENSOR_SERVICE);
        s=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        restaurantLocations.add(new LatLng(0.11,80.7700));  // add restaurant locations through HTTP call
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
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        sm.unregisterListener(this);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        String value="Sensor Value : " + values[0];
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
        sendSensorData(values[0],currentRestaurant);
    }

    private void sendSensorData(float value,int restaurantID){
        String url = "http://httpbin.org/post";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    Log.d("Response", response);
                }, error -> {
                    // error
                    Log.d("Error.Response", String.valueOf(error));
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id", String.valueOf(restaurantID));
                params.put("value", String.valueOf(value));
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void receiveRestaurantData(float value,int restaurantID){
        String url = "http://httpbin.org/post";
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,null,
                response -> {
                    // response
                    Log.d("Response", response.toString());
                }, error -> {
            // error
            Log.d("Error.Response", String.valueOf(error));
        }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id", String.valueOf(restaurantID));
                params.put("value", String.valueOf(value));
                return params;
            }
        };
        queue.add(postRequest);

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
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng sLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(checkRange(sLatLng)){
            sm.registerListener(this,s, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            sm.unregisterListener(this);
        }
    }

    protected boolean checkRange(LatLng sLatLng){

        for(LatLng dLatLng:restaurantLocations){
            if(Math.acos(Math.sin(1.3963) * Math.sin(dLatLng.latitude) + Math.cos(sLatLng.latitude) *
                    Math.cos(dLatLng.latitude) * Math.cos(dLatLng.longitude - (sLatLng.longitude))) * 6371 <= 0.1)
            {
                return true;
            }
        }
        return false;

    }
}