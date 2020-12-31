package com.example.personeltakip;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Following extends AppCompatActivity {

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

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        init();
        tvFirmCode.setText(Tools.PersonelData.FirmCode);
        tvFirmCity.setText("(" + Tools.PersonelData.FirmCity + ")");
        tvFirmTitle.setText(Tools.PersonelData.FirmTitle);
        tvCitizenNumber.setText(Tools.PersonelData.CitizenNumber.substring(0, 4) + "..." + tools.RightStr(Tools.PersonelData.CitizenNumber, 3));
        tvNameSurname.setText(Tools.PersonelData.Name + " " + Tools.PersonelData.Surname);
    }
}