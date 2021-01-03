package com.example.personeltakip;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetLastLocation extends Service {
    public static class PersonelData {
        public static String CitizenNumber;
        public static String Name;
        public static String Surname;
        public static String FirmCode;
        public static String FirmTitle;
        public static String FirmCity;
        public static boolean isAdmin;
    }

    public static Context context;
    public static int delay = 3000;
    final String NAMESPACE = "http://www.yasinkaratas.com.tr/";
    final String URL = "http://www.yasinkaratas.com.tr/WebServices/PersonelTakip.asmx";
    String METHOD_NAME = "SaveLocation";
    String SOAP_ACTION = NAMESPACE + METHOD_NAME;// "http://www.yasinkaratas.com.tr/HelloWorld";

    public static class CurrentPosition {
        public static Double Latitude = 40.64;
        public static Double Longitude = 29.23;
        public static float Speed = 0.0f;
        public static String DateTime = "";
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
                    Location location = locationResult.getLastLocation();
                    CurrentPosition.Latitude = location.getLatitude();
                    CurrentPosition.Longitude = location.getLongitude();
                    CurrentPosition.Speed = location.getSpeed();
                    Date date = new Date(location.getTime());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    CurrentPosition.DateTime = sdf.format(date);
                    String msg = "\nLAT: " + CurrentPosition.Latitude + "\nLONG: " + CurrentPosition.Longitude
                            + "\nTIME: " + GetLastLocation.CurrentPosition.DateTime + "\nSPEED: " + String.valueOf(CurrentPosition.Speed);
                    Log.w("SPEEDY-1", msg);

                    new asynTask().execute();
                }
            }
        };

        fusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Processing when the API call is successful.
                        Log.w("SPEEDY-2", CurrentPosition.Latitude.toString() + ", " + CurrentPosition.Longitude.toString());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.w("SPEEDY-3", CurrentPosition.Latitude.toString() + ", " + CurrentPosition.Longitude.toString());
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


    private class asynTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        String responsedData = "";
        SoapObject response;
        String[] rd;

        @Override
        protected Void doInBackground(String... strings) {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("firmCode", PersonelData.FirmCode);
            Request.addProperty("userCode", PersonelData.CitizenNumber);
            Request.addProperty("dateTime", CurrentPosition.DateTime);
            Request.addProperty("Lat", String.valueOf(CurrentPosition.Latitude));
            Request.addProperty("Lon", String.valueOf(CurrentPosition.Longitude));
            Request.addProperty("Speed", String.valueOf(CurrentPosition.Speed));
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(Request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                response = (SoapObject) envelope.bodyIn;
                rd = response.getProperty(0).toString().replaceAll("string=", "").split(";");
                rd[0] = rd[0].replaceAll("[^0-9]", "");
                responsedData = rd[0];
            } catch (Exception e) {
                responsedData = "0";
                e.printStackTrace();
            }

            if (METHOD_NAME.equals("SaveLocation")) {
                if (responsedData.equals("1")) {
                    String msg = "\nLAT: " + CurrentPosition.Latitude + "\nLONG: " + CurrentPosition.Longitude
                            + "\nTIME: " + GetLastLocation.CurrentPosition.DateTime + "\nSPEED: " + String.valueOf(CurrentPosition.Speed);
                    Log.w("KAYDEDİLDİ-2", msg);
                } else {
                    String msg = "\nLAT: " + CurrentPosition.Latitude + "\nLONG: " + CurrentPosition.Longitude
                            + "\nTIME: " + GetLastLocation.CurrentPosition.DateTime + "\nSPEED: " + String.valueOf(CurrentPosition.Speed);
                    Log.w("KAYDEDİLMEDİ-2", msg);
                }
            }

            return null;
        }

        // Arka plan işlemleri bittikten sonra yapılacaklar..
        @Override
        protected void onPostExecute(Void aVoid) {
            if (METHOD_NAME.equals("SaveLocation")) {
                switch (responsedData) {
                    case "1": {
                        String msg = "\nLAT: " + CurrentPosition.Latitude + "\nLONG: " + CurrentPosition.Longitude
                                + "\nTIME: " + GetLastLocation.CurrentPosition.DateTime + "\nSPEED: " + String.valueOf(CurrentPosition.Speed);
                        Log.w("KAYDEDİLDİ-2", msg);

                        break;
                    }
                    case "0": {
                        break;
                    }
                }
            }
        }
    }

}