package com.example.personeltakip;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txtCitizenshipNumber;
    EditText txtFirmCode;
    EditText txtPassword;
    Button btnLogin;
    Boolean isLoggedIn;
    Tools tools;

    void init() {
        tools = new Tools();
        txtCitizenshipNumber = findViewById(R.id.txtCitizenshipNumber);
        txtFirmCode = findViewById(R.id.txtFirmCode);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
    }

    void CheckPermissions() {
        // Dynamically apply for required permissions if the API level is 28 or smaller.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

            ) {
                String[] strings =
                        {
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        };
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            // Dynamically apply for required permissions if the API level is greater than 28. The android.permission.ACCESS_BACKGROUND_LOCATION permission is required.
            if (
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        CheckPermissions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if (txtPassword.getText().length() == 0) {
                    Toast.makeText(this, "Lütfen geçerli bir şifre girin.", Toast.LENGTH_LONG).show();
                    txtPassword.requestFocus();
                    txtPassword.selectAll();
                    return;
                }
                if (txtFirmCode.getText().length() != 6) {
                    Toast.makeText(this, "Firma kodu hatalıdır.", Toast.LENGTH_LONG).show();
                    txtFirmCode.requestFocus();
                    txtFirmCode.selectAll();
                    return;
                }
                if (!tools.TCConfirm(txtCitizenshipNumber.getText().toString())) {
                    Toast.makeText(this, "Kimlik numarası hatalıdır.", Toast.LENGTH_LONG).show();
                    txtCitizenshipNumber.requestFocus();
                    txtCitizenshipNumber.selectAll();
                    return;
                }
                new asynTask().execute();
                break;

            default:
                break;
        }
    }

    private class asynTask extends AsyncTask<String, Void, Void> {
        String resultText;
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        AlertDialog alert;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Lütfen bekleyin...");
            progressDialog.show();
        }

        int responsedData = 0;

        @Override
        protected Void doInBackground(String... strings) {

/*
            String NAMESPACE = "http://tckimlik.nvi.gov.tr/WS";
            String URL = "https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx";
            String SOAP_ACTION = "http://tckimlik.nvi.gov.tr/WS/TCKimlikNoDogrula";
            String METHOD_NAME = "TCKimlikNoDogrula";
*/

            resultText = "Logged In";
            String NAMESPACE = "http://www.yasinkaratas.com.tr/";
            String URL = "http://www.yasinkaratas.com.tr/WebServices/PersonelTakip.asmx";
            String METHOD_NAME = "Login";
            String SOAP_ACTION = NAMESPACE + METHOD_NAME;// "http://www.yasinkaratas.com.tr/HelloWorld";

            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
            Request.addProperty("firmCode", txtFirmCode.getText().toString());
            Request.addProperty("userCode", txtCitizenshipNumber.getText().toString());
            Request.addProperty("password", tools.MD5Generator("+-*" + txtPassword.getText().toString() + "*-+").toString());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.implicitTypes = true;
            envelope.setOutputSoapObject(Request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject response = (SoapObject) envelope.bodyIn;
                responsedData = Integer.parseInt(response.getProperty(0).toString());
                isLoggedIn = responsedData == 1;

                if (responsedData == 0) {
                    resultText = "Giriş işlemi başarısız olmuştur. Lütfen bilgilerinizi kontrol edip tekrar deneyin!";
                } else if (responsedData == 1) {
                    resultText = "Giriş işlemi başarıyla tamamlanmıştır!";
                } else if (responsedData == 2) {
                    resultText = "İlk defa şifre belirleme işlemi yapmak istiyor musunuz?";
                } else if (responsedData == 3) {
                    resultText = "Çoklu kullanıcı hatası?";
                }
            } catch (Exception e) {
                resultText = "Bilinmeyen bir hata meydana geldi. Lütfen programcınızla görüşün";
                e.printStackTrace();
            }
            return null;
        }

        // Arka plan işlemleri bittikten sonra yapılacaklar..
        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            switch (responsedData) {
                case 1: {
                    Intent intent = new Intent();
                    break;
                }
                case 0:
                case 3: {
                    alert = builder.setMessage(resultText)
                            .setTitle("HATA!")
                            .setCancelable(true)
                            .setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create();
                    alert.show();
                    break;
                }
                case 2: {
                    alert = builder.setMessage(resultText)
                            .setTitle("ONAY!")
                            .setCancelable(true)
                            .setPositiveButton("TAMAM", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create();
                    alert.show();
                    break;
                }
            }
        }
    }
}