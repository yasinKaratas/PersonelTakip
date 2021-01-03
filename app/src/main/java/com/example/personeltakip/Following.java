package com.example.personeltakip;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class Following extends AppCompatActivity implements OnMapReadyCallback {

    final String NAMESPACE = "http://www.yasinkaratas.com.tr/";
    final String URL = "http://www.yasinkaratas.com.tr/WebServices/PersonelTakip.asmx";
    String METHOD_NAME = "GetLocations";
    String SOAP_ACTION = NAMESPACE + METHOD_NAME;// "http://www.yasinkaratas.com.tr/HelloWorld";
    List<String> lastLocations;

    private static final String TAG = "MapViewDemoActivity";
    private HuaweiMap hMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    double oldLatitude = 40.64;
    double oldLongitude = 29.23;

    //TextView tvFirmCode;
    TextView tvFirmTitle;
    TextView tvFirmCity;
    TextView tvCitizenNumber;
    TextView tvNameSurname;
    Tools tools;


    void init() {
        //tvFirmCode = findViewById(R.id.tvFirmCode);
        tvFirmTitle = findViewById(R.id.tvFirmTitle);
        tvFirmCity = findViewById(R.id.tvFirmCity);
        tvCitizenNumber = findViewById(R.id.tvCitizenNumber);
        tvNameSurname = findViewById(R.id.tvNameSurname);
        lastLocations = new ArrayList<String>();
        tools = new Tools();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        MapView mMapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        MapsInitializer.setApiKey("CgB6e3x9Iw723tzF8QD9fwWo5XUkLjhgA4HXJcDquOdwHo025PBgikBFgdPx8EiAgiqix1XHyHEW/gzh7uOt83PS");
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        init();

        //tvFirmCode.setText(Tools.PersonelData.FirmCode);
        tvFirmCity.setText("(" + GetLastLocation.PersonelData.FirmCity + ")");
        tvFirmTitle.setText(GetLastLocation.PersonelData.FirmTitle);
        tvCitizenNumber.setText(GetLastLocation.PersonelData.CitizenNumber.substring(0, 3) + "..." + tools.RightStr(GetLastLocation.PersonelData.CitizenNumber, 3));
        tvNameSurname.setText(GetLastLocation.PersonelData.Name + " " + GetLastLocation.PersonelData.Surname);
        GetLastLocation.context = this;
        startForegroundService(new Intent(this, GetLastLocation.class));
        startTime();
    }

    public void addMarker(double lat, double lng, String title, String snip) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title)
                .snippet(snip)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.mymarker))
                .clusterable(true);
        hMap.addMarker(options).showInfoWindow();
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        hMap.setMyLocationEnabled(true);
        hMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    //private Handler mHandler = new Handler();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private void startTime() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, GetLastLocation.delay);
    }

    /*
    handler.post(new Runnable() {
            public void run() {
                // your UI code goes here
            }
        });
    */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            mHandler.postDelayed(this, GetLastLocation.delay);
//            if ((oldLatitude == GetLastLocation.CurrentPosition.Latitude) && (oldLongitude == GetLastLocation.CurrentPosition.Longitude)) {
//                return;
//            }
            oldLatitude = GetLastLocation.CurrentPosition.Latitude;
            oldLongitude = GetLastLocation.CurrentPosition.Longitude;
//            String msg = "\nLAT: " + GetLastLocation.CurrentPosition.Latitude + "\nLONG: " + GetLastLocation.CurrentPosition.Longitude
//                    + "\nTIME: " + GetLastLocation.CurrentPosition.DateTime + "\nSPEED: " + String.valueOf(GetLastLocation.CurrentPosition.Speed);
//            Toast.makeText(Following.this, msg, Toast.LENGTH_LONG).show();
            if (GetLastLocation.PersonelData.isAdmin) {
                new asynTask().execute();
                hMap.clear();
                for (int i = 0; i < lastLocations.size(); i++) {
                    String[] person = lastLocations.get(i).split("\\|");
                    String time = person[0].trim();
                    Double lat = Double.parseDouble(person[1].toString().trim());
                    Double lon = Double.parseDouble(person[2].toString().trim());
                    String speed = person[3].trim();
                    String name = person[4].trim();
                    String surname = person[5].trim();
                    addMarker(lat, lon, name + " " + surname, "HIZ: " + speed);
                }
            } else {
                hMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        LatLng(
                        GetLastLocation.CurrentPosition.Latitude,
                        GetLastLocation.CurrentPosition.Longitude), 17f));

            }
        }
    };


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
            Request.addProperty("firmCode", GetLastLocation.PersonelData.FirmCode);
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

            if (METHOD_NAME.equals("GetLocations")) {
                if (responsedData.equals("1")) {
                    Log.w("VERİLER1", "GELDİ");
                    lastLocations.clear();
                    for (int i = 1; i < rd.length - 2; i++) {
                        //            0                         1                 2                     3                   4                     5
                        //tblocations.lc_date_time, tblocations.lc_lat, tblocations.lc_lon, tblocations.lc_speed, tbworkers.wk_name, tbworkers.wk_surname
                        lastLocations.add(rd[i]);
                    }
                } else {
                    Log.w("VERİLER1", "GELMEDİ");
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
                        String msg = "\nLAT: " + GetLastLocation.CurrentPosition.Latitude + "\nLONG: " + GetLastLocation.CurrentPosition.Longitude
                                + "\nTIME: " + GetLastLocation.CurrentPosition.DateTime + "\nSPEED: " + String.valueOf(GetLastLocation.CurrentPosition.Speed);
                        Log.w("KAYDEDİLDİ-2", msg);

                        break;
                    }
                    case "0": {
                        break;
                    }
                }
            } else if (METHOD_NAME.equals("GetLocations")) {
                switch (responsedData) {
                    case "1": {
                        String msg = "\nLAT: " + GetLastLocation.CurrentPosition.Latitude + "\nLONG: " + GetLastLocation.CurrentPosition.Longitude
                                + "\nTIME: " + GetLastLocation.CurrentPosition.DateTime + "\nSPEED: " + String.valueOf(GetLastLocation.CurrentPosition.Speed);
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