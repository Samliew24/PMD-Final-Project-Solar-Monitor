package com.example.solarmonitorapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NotificationsActivity extends AppCompatActivity {

    Switch switchMain, switchWeather, switchEnergy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        switchMain = findViewById(R.id.switchMain);
        switchWeather = findViewById(R.id.switchWeather);
        switchEnergy = findViewById(R.id.switchEnergy);

        // Main switch control
        switchMain.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "Notifications Enabled" : "Notifications Disabled", Toast.LENGTH_SHORT).show();
        });

        switchWeather.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "Weather Alerts ON" : "Weather Alerts OFF", Toast.LENGTH_SHORT).show();
        });

        switchEnergy.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, isChecked ? "Energy Alerts ON" : "Energy Alerts OFF", Toast.LENGTH_SHORT).show();
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                return true;
            } else if (id == R.id.nav_efficiency) {
                startActivity(new Intent(this, RealTimeEfficiencyActivity.class));
                return true;
            } else if (id == R.id.nav_production) {
                startActivity(new Intent(this, PredictionActivity.class));
                return true;
            } else if (id == R.id.nav_impact) {
                startActivity(new Intent(this, EnvironmentalImpactActivity.class));
                return true;
            } else if (id == R.id.nav_reports) {
                startActivity(new Intent(this, ReportActivity.class));
                return true;
            }
            return false;
        });
    }
}
