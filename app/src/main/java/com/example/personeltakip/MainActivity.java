package com.example.personeltakip;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final String NAMESPACE = "http://www.yasinkaratas.com.tr/";
    final String URL = "http://www.yasinkaratas.com.tr/WebServices/PersonelTakip.asmx";
    String METHOD_NAME = "Login";
    String SOAP_ACTION = NAMESPACE + METHOD_NAME;// "http://www.yasinkaratas.com.tr/HelloWorld";
    String resultText = "";

    EditText etCitizenshipNumber;
    EditText etFirmCode;
    EditText etPassword;
    TextView tvForgetPassword;
    Button btnLogin;
    Tools tools;

    void init() {
        tools = new Tools();

        etCitizenshipNumber = findViewById(R.id.etCitizenshipNumber);
        etFirmCode = findViewById(R.id.etFirmCode);
        etPassword = findViewById(R.id.etPassword);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
    }

    boolean CheckPermissions() {
        // Dynamically apply for required permissions if the API level is 28 or smaller.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

            ) {
                String[] strings =
                        {
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        };
                ActivityCompat.requestPermissions(this, strings, 1);
            }
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"
                };
                ActivityCompat.requestPermissions(this, strings, 2);
            }
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_BACKGROUND_LOCATION") == PackageManager.PERMISSION_GRANTED;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        etPassword.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin: {
                if (!tools.isInternetAvailable()) {
                    Toast.makeText(this, R.string.internetUnavailable, Toast.LENGTH_LONG).show();
                    etPassword.requestFocus();
                    etPassword.selectAll();
                    return;
                }
                if (etPassword.getText().length() < 4) {
                    Toast.makeText(this, R.string.wrongPassword, Toast.LENGTH_LONG).show();
                    etPassword.requestFocus();
                    etPassword.selectAll();
                    return;
                }
                if (etFirmCode.getText().length() != 6) {
                    Toast.makeText(this, R.string.wrongFirmCode, Toast.LENGTH_LONG).show();
                    etFirmCode.requestFocus();
                    etFirmCode.selectAll();
                    return;
                }
                if (!tools.TCConfirm(etCitizenshipNumber.getText().toString())) {
                    Toast.makeText(this, R.string.errorCitizenNumber, Toast.LENGTH_LONG).show();
                    etCitizenshipNumber.requestFocus();
                    etCitizenshipNumber.selectAll();
                    return;
                }
                METHOD_NAME = "Login";
                SOAP_ACTION = NAMESPACE + METHOD_NAME;
                new asynTask().execute();
                break;
            }
            case R.id.tvForgetPassword: {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                AlertDialog alert =
                        builder.setMessage(R.string.resetPasswordMessage)
                                .setTitle(R.string.information)
                                .setCancelable(true)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).create();
                alert.show();

                break;
            }
            default: {
                break;
            }
        }
    }

    private class asynTask extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        AlertDialog alert;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage(getString(R.string.pleaseWait));
            progressDialog.show();
        }

        String responsedData = "";
        SoapObject response;
        String[] rd;

        @Override
        protected Void doInBackground(String... strings) {

/*
            String NAMESPACE = "http://tckimlik.nvi.gov.tr/WS";
            String URL = "https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx";
            String SOAP_ACTION = "http://tckimlik.nvi.gov.tr/WS/TCKimlikNoDogrula";
            String METHOD_NAME = "TCKimlikNoDogrula";
*/

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("firmCode", etFirmCode.getText().toString());
            Request.addProperty("userCode", etCitizenshipNumber.getText().toString());
            Request.addProperty("password", tools.MD5Generator("+-*" + etPassword.getText().toString() + "*-+").toString());
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
                responsedData = "1";
                e.printStackTrace();
            }

            if (METHOD_NAME.equals("Login")) {
                tools.isLoggedIn = responsedData.equals("1");
                if (responsedData.equals("1")) {
                    resultText = getString(R.string.loggedIn);
                } else if (responsedData.equals("0")) {
                    resultText = getString(R.string.notLoggedIn);
                } else if (responsedData.equals("2")) {
                    resultText = getString(R.string.noPassword);
                } else if (responsedData.equals("3")) {
                    resultText = getString(R.string.multiplyUserError);
                } else if (responsedData.equals("*")) {
                    resultText = getString(R.string.unknownError);
                }
            } else if (METHOD_NAME.equals("SetPassword")) {
                if (responsedData.equals("1")) {
                    resultText = getString(R.string.passwordDefined);
                } else if (responsedData.equals("0")) {
                    resultText = getString(R.string.passwordDefineError);
                } else if (responsedData.equals("*")) {
                    resultText = getString(R.string.unknownError);
                }
            }
            return null;
        }

        // Arka plan işlemleri bittikten sonra yapılacaklar..
        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            if (METHOD_NAME.equals("Login")) {
                switch (responsedData) {
                    case "1": {
                        Tools.PersonelData.CitizenNumber = rd[1];
                        Tools.PersonelData.Name = rd[2];
                        Tools.PersonelData.Surname = rd[3];
                        Tools.PersonelData.FirmCode = rd[4];
                        Tools.PersonelData.FirmTitle = rd[5];
                        Tools.PersonelData.FirmCity = rd[6];

                        if (!CheckPermissions()) {
                            alert = builder.setMessage(R.string.permissionErrorMessage)
                                    .setTitle(R.string.error)
                                    .setCancelable(true)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            CheckPermissions();
                                        }
                                    }).create();
                            alert.show();
                            return;
                        }
                        if (!isGpsEnabled()) {
                            alert = builder.setMessage("Lütfen GPS ayarınızı açın.")
                                    .setTitle(R.string.information)
                                    .setCancelable(true)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        }
                                    }).create();
                            alert.show();
                            return;
                        }
                        Intent intent = new Intent(MainActivity.this, Following.class);
                        startActivity(intent);
                        //finish();
                        break;
                    }
                    case "*":
                    case "0":
                    case "3": {
                        alert = builder.setMessage(resultText)
                                .setTitle(R.string.error)
                                .setCancelable(true)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).create();
                        alert.show();
                        break;
                    }
                    case "2": {
                        alert = builder.setMessage(resultText)
                                .setTitle(R.string.confirmation)
                                .setCancelable(false)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        alert = builder.setMessage(R.string.passwordCaution)
                                                .setTitle(R.string.confirmation)
                                                .setCancelable(false)
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        METHOD_NAME = "SetPassword";
                                                        SOAP_ACTION = NAMESPACE + METHOD_NAME;
                                                        new asynTask().execute();
                                                    }
                                                })
                                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                    }
                                                }).create();
                                        alert.show();
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                }).create();
                        alert.show();
                        break;
                    }
                }
            } else if (METHOD_NAME.equals("SetPassword")) {
                alert = builder.setMessage(resultText)
                        .setTitle(R.string.information)
                        .setCancelable(true)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create();
                alert.show();

            }
        }
    }

    public boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}