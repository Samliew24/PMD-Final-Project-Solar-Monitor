package com.example.solarmonitorapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ReportActivity extends AppCompatActivity {

    TextView tvCurrentOutput, tvTotalGeneration, tvPeakPower, tvPeakHour;
    DatabaseHelper dbHelper;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize views
        tvCurrentOutput = findViewById(R.id.tvCurrentOutput);
        tvTotalGeneration = findViewById(R.id.tvTotalGeneration);
        tvPeakPower = findViewById(R.id.tvPeakPower);
        tvPeakHour = findViewById(R.id.tvPeakHour);

        dbHelper = new DatabaseHelper(this);

        loadReportData();

        // Bell icon to open NotificationsActivity
        ImageView imgBell = findViewById(R.id.imgBell);
        imgBell.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));

        // Bottom navigation setup
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_reports);

        nav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_dashboard) {
                    startActivity(new Intent(ReportActivity.this, DashboardActivity.class));
                    return true;
                } else if (id == R.id.nav_efficiency) {
                    startActivity(new Intent(ReportActivity.this, RealTimeEfficiencyActivity.class));
                    return true;
                } else if (id == R.id.nav_production) {
                    startActivity(new Intent(ReportActivity.this, PredictionActivity.class));
                    return true;
                } else if (id == R.id.nav_impact) {
                    startActivity(new Intent(ReportActivity.this, EnvironmentalImpactActivity.class));
                    return true;
                } else if (id == R.id.nav_reports) {
                    return true;
                }
                return false;
            }
        });
    }

    private void loadReportData() {
        // Current Output (latest energy)
        Cursor latest = dbHelper.getLatestEnergyData();
        if (latest != null && latest.moveToFirst()) {
            float latestEnergy = latest.getFloat(2);
            tvCurrentOutput.setText(latestEnergy + " kW");
            latest.close();
        } else {
            tvCurrentOutput.setText("--");
        }

        // Total generation
        Cursor total = dbHelper.getTotalEnergy();
        if (total != null && total.moveToFirst()) {
            float totalEnergy = total.getFloat(0);
            tvTotalGeneration.setText(totalEnergy + " kWh");
            total.close();
        } else {
            tvTotalGeneration.setText("--");
        }

        // Peak Power
        Cursor peakPower = dbHelper.getPeakPower();
        if (peakPower != null && peakPower.moveToFirst()) {
            float peak = peakPower.getFloat(0);
            tvPeakPower.setText(peak + " kW");
            peakPower.close();
        } else {
            tvPeakPower.setText("--");
        }

        // Peak Hour
        Cursor peakHour = dbHelper.getPeakHour();
        if (peakHour != null && peakHour.moveToFirst()) {
            String hour = peakHour.getString(0);
            tvPeakHour.setText(hour);
            peakHour.close();
        } else {
            tvPeakHour.setText("--");
        }
    }
}
