// EnvironmentalImpactActivity.java
package com.example.solarmonitorapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EnvironmentalImpactActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environmental_impact);

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_impact);

        nav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                return true;
            } else if (itemId == R.id.nav_efficiency) {
                startActivity(new Intent(getApplicationContext(), RealTimeEfficiencyActivity.class));
                return true;
            } else if (itemId == R.id.nav_production) {
                startActivity(new Intent(getApplicationContext(), PredictionActivity.class));
                return true;
            } else if (itemId == R.id.nav_impact) {
                return true;
            } else if (itemId == R.id.nav_reports) {
                startActivity(new Intent(getApplicationContext(), ReportActivity.class));
                return true;
            }
            return false;
        });

        ImageView imgBell = findViewById(R.id.imgBell);
        imgBell.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });
    }
}
