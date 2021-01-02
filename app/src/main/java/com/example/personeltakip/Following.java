package com.example.personeltakip;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

public class Following extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapViewDemoActivity";
    private HuaweiMap hMap;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    double oldLatitude = 40.6436629;
    double oldLongitude = 29.2311684;

    TextView tvFirmCode;
    TextView tvFirmTitle;
    TextView tvFirmCity;
    TextView tvCitizenNumber;
    TextView tvNameSurname;
    Tools tools;


    void init() {
        tvFirmCode = findViewById(R.id.tvFirmCode);
        tvFirmTitle = findViewById(R.id.tvFirmTitle);
        tvFirmCity = findViewById(R.id.tvFirmCity);
        tvCitizenNumber = findViewById(R.id.tvCitizenNumber);
        tvNameSurname = findViewById(R.id.tvNameSurname);
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
        applyPermission();
        init();

        tvFirmCode.setText(Tools.PersonelData.FirmCode);
        tvFirmCity.setText("(" + Tools.PersonelData.FirmCity + ")");
        tvFirmTitle.setText(Tools.PersonelData.FirmTitle);
        tvCitizenNumber.setText(Tools.PersonelData.CitizenNumber.substring(0, 4) + "..." + tools.RightStr(Tools.PersonelData.CitizenNumber, 3));
        tvNameSurname.setText(Tools.PersonelData.Name + " " + Tools.PersonelData.Surname);
        GetLastLocation.context = this;
        startForegroundService(new Intent(this, GetLastLocation.class));
        startTime();
    }


    public void applyPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk <= 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    public void addMarker(double lat, double lng, String title, String snip) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title)
                .snippet(snip)
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.mymarker))
                .clusterable(true);
        hMap.addMarker(options);
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        hMap.setMyLocationEnabled(true);
        hMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private Handler mHandler = new Handler();

    private void startTime() {
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, GetLastLocation.delay);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            mHandler.postDelayed(this, GetLastLocation.delay);
            if ((oldLatitude == GetLastLocation.CurrentPosition.Latitude) && (oldLongitude == GetLastLocation.CurrentPosition.Longitude)) {
                return;
            }
            oldLatitude = GetLastLocation.CurrentPosition.Latitude;
            oldLongitude = GetLastLocation.CurrentPosition.Longitude;
            String msg = oldLatitude + ", " + oldLongitude;
            Toast.makeText(Following.this, msg, Toast.LENGTH_LONG).show();
            hMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                    LatLng(
                    GetLastLocation.CurrentPosition.Latitude,
                    GetLastLocation.CurrentPosition.Longitude), 17f));
        }
    };

}