package com.example.personeltakip;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;

public class GetLastLocation extends Service {
    public static Context context;
    public static int delay = 5000;

    public static class CurrentPosition {
        public static Double Latitude = 40.6436629;
        public static Double Longitude = 29.2311684;
    }

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(delay);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(1f);


        LocationCallback mLocationCallback;
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    CurrentPosition.Latitude = locationResult.getLastLocation().getLatitude();
                    CurrentPosition.Longitude = locationResult.getLastLocation().getLongitude();
                    //Log.w(TAG + "1", CurrentPosition.Latitude.toString() + ", " + CurrentPosition.Longitude.toString());
                }
            }
        };

        fusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Processing when the API call is successful.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Processing when the API call fails.
                    }
                });

// Obtain the last known location.
        Task<Location> task = fusedLocationProviderClient.getLastLocation()
                // Define callback for success in obtaining the last known location.
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            return;
                        }
                        // Define logic for processing the Location object upon success.
                        // ...
                        //Double _Latitude = location.getLatitude();
                        //Double _Longitude = location.getLongitude();
                        //Log.w(TAG + "2", _Latitude.toString() + ", " + _Longitude.toString());
                    }
                })
                // Define callback for failure in obtaining the last known location.
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // ...
                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }
}