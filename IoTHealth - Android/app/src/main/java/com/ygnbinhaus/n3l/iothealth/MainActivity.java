package com.ygnbinhaus.n3l.iothealth;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Button settingBtn = (Button) findViewById(R.id.setting);
        final Button historyBtn = (Button) findViewById(R.id.history);
        final Button measureBtn = (Button) findViewById(R.id.measure);
        final Button doctorBtn = (Button) findViewById(R.id.doctor);
        final Button emergencyBtn = (Button) findViewById(R.id.emergency);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent historyIntent = new Intent(getApplicationContext(), History.class);
                startActivity(historyIntent);
            }
        });
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingIntent);
            }
        });
        measureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent measureLocationIntent = new Intent(getApplicationContext(), MeasureLocation.class);
                startActivity(measureLocationIntent);
            }
        });
        doctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + preferences.getString("doctor_number","")));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });
        emergencyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SmsManager sms = SmsManager.getDefault();
                String msg = String.format("Patient %s: email-%s and NRC-%s is in emergency situation.", preferences.getString("name",""), preferences.getString("email", ""), preferences.getString("nrc", ""));
                sms.sendTextMessage(preferences.getString("doctor_number",""), null, msg, null, null);
                sms.sendTextMessage(preferences.getString("hospital_number",""), null, msg, null, null);
                Toast.makeText(getApplicationContext(),"EMERGENCY MESSAGE SENT",Toast.LENGTH_SHORT);
            }
        });
    }
}
