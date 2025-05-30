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

public class PredictionActivity extends AppCompatActivity {

    TextView tvWeatherData, tvProductionData, tvPredictedOutput, tvEfficiency, tvHourlyData;
    DatabaseHelper dbHelper;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        // Initialize Views
        tvWeatherData = findViewById(R.id.tvWeatherData);
        tvProductionData = findViewById(R.id.tvProductionData);
        tvPredictedOutput = findViewById(R.id.tvPredictedOutput);
        tvEfficiency = findViewById(R.id.tvEfficiency);
        tvHourlyData = findViewById(R.id.tvHourlyData);
        dbHelper = new DatabaseHelper(this);

        // Bell icon to Notifications
        ImageView imgBell = findViewById(R.id.imgBell);
        imgBell.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        // Load real data from SQLite
        loadRealPredictionData();

        // Bottom Navigation
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_production);

        nav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_dashboard) {
                    startActivity(new Intent(PredictionActivity.this, DashboardActivity.class));
                    return true;
                } else if (id == R.id.nav_efficiency) {
                    startActivity(new Intent(PredictionActivity.this, RealTimeEfficiencyActivity.class));
                    return true;
                } else if (id == R.id.nav_production) {
                    return true;
                } else if (id == R.id.nav_impact) {
                    startActivity(new Intent(PredictionActivity.this, EnvironmentalImpactActivity.class));
                    return true;
                } else if (id == R.id.nav_reports) {
                    startActivity(new Intent(PredictionActivity.this, ReportActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void loadRealPredictionData() {
        Cursor cursor = dbHelper.getLatestEnergyData();

        if (cursor != null && cursor.moveToFirst()) {
            float energy = cursor.getFloat(2);         // Energy column
            float efficiency = cursor.getFloat(3);     // Efficiency column

            // Set dynamic content
            tvWeatherData.setText("🌤️ Mostly Sunny\nTemperature: 32°C\nHumidity: 45%");
            tvProductionData.setText("⚡ Estimated Output: " + energy + " kWh\nPanel Temp: 45°C\nCloud Cover: Low");
            tvPredictedOutput.setText("Predicted Output:\n" + energy + " kWh");
            tvEfficiency.setText("Efficiency:\n" + efficiency + "%");

            // Simulated hourly based on energy value
            float morning = energy * 0.3f;
            float noon = energy * 0.5f;
            float afternoon = energy * 0.2f;

            String hourly = "⏰ 10 AM: " + String.format("%.2f", morning) + " kW\n" +
                    "⏰ 12 PM: " + String.format("%.2f", noon) + " kW\n" +
                    "⏰ 2 PM: " + String.format("%.2f", afternoon) + " kW";
            tvHourlyData.setText(hourly);

            cursor.close();
        } else {
            tvWeatherData.setText("No data available.");
            tvProductionData.setText("No prediction data.");
            tvPredictedOutput.setText("Predicted Output:\n--");
            tvEfficiency.setText("Efficiency:\n--");
            tvHourlyData.setText("--");
        }
    }
}
