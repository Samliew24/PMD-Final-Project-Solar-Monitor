package com.example.solarmonitorapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private LineChart lineChart;
    private TextView btnDay, btnWeek, btnMonth;
    private DatabaseHelper dbHelper;

    private TextView tvWelcome, tvCurrentOutput, tvWeather, tvPeakPower, tvPeakHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvCurrentOutput = findViewById(R.id.tvCurrentOutput);
        tvWeather = findViewById(R.id.tvWeather);
        tvPeakPower = findViewById(R.id.tvPeakPower);
        tvPeakHours = findViewById(R.id.tvPeakHours);

        lineChart = findViewById(R.id.lineChart);
        btnDay = findViewById(R.id.btnDay);
        btnWeek = findViewById(R.id.btnWeek);
        btnMonth = findViewById(R.id.btnMonth);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) return true;
            else if (id == R.id.nav_efficiency)
                startActivity(new Intent(this, RealTimeEfficiencyActivity.class));
            else if (id == R.id.nav_production)
                startActivity(new Intent(this, PredictionActivity.class));
            else if (id == R.id.nav_impact)
                startActivity(new Intent(this, EnvironmentalImpactActivity.class));
            else if (id == R.id.nav_reports)
                startActivity(new Intent(this, ReportActivity.class));
            return true;
        });

        ImageView imgBell = findViewById(R.id.imgBell);
        imgBell.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));

        loadTopData(); // <-- fix top part
        loadEnergyChartData("Day");
        highlightSelectedButton(btnDay, btnWeek, btnMonth);

        btnDay.setOnClickListener(v -> {
            loadEnergyChartData("Day");
            highlightSelectedButton(btnDay, btnWeek, btnMonth);
        });

        btnWeek.setOnClickListener(v -> {
            loadEnergyChartData("Week");
            highlightSelectedButton(btnWeek, btnDay, btnMonth);
        });

        btnMonth.setOnClickListener(v -> {
            loadEnergyChartData("Month");
            highlightSelectedButton(btnMonth, btnDay, btnWeek);
        });
    }

    private void loadTopData() {
        tvWelcome.setText("Welcome, User! 🌞");
        tvWeather.setText("Partly Cloudy, 31°C");

        Cursor latest = dbHelper.getLatestEnergyData();
        if (latest != null && latest.moveToFirst()) {
            float energy = latest.getFloat(latest.getColumnIndexOrThrow("energy"));
            tvCurrentOutput.setText(String.format("%.0f W", energy));
        } else {
            tvCurrentOutput.setText("--");
        }

        Cursor peak = dbHelper.getPeakPower();
        if (peak != null && peak.moveToFirst()) {
            float peakPower = peak.getFloat(0);
            tvPeakPower.setText(String.format("%.0f W", peakPower));
        } else {
            tvPeakPower.setText("--");
        }

        Cursor peakTime = dbHelper.getPeakHour();
        if (peakTime != null && peakTime.moveToFirst()) {
            String date = peakTime.getString(0);
            tvPeakHours.setText(date + " (approx)");
        } else {
            tvPeakHours.setText("--");
        }
    }

    private void highlightSelectedButton(TextView selected, TextView... others) {
        selected.setBackgroundResource(R.drawable.rounded_button);
        selected.setTextColor(Color.BLACK);
        for (TextView btn : others) {
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setTextColor(Color.BLACK);
        }
    }

    private void loadEnergyChartData(String rangeType) {
        List<Entry> entries = new ArrayList<>();
        Cursor cursor;

        String startDate = getStartDate(rangeType);
        cursor = dbHelper.getEnergyDataByDateRange(startDate);

        int index = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                float energy = cursor.getFloat(cursor.getColumnIndexOrThrow("energy"));
                entries.add(new Entry(index++, energy));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (entries.isEmpty()) entries.add(new Entry(0, 0));

        LineDataSet dataSet = new LineDataSet(entries, "Energy Produced (W)");
        dataSet.setColor(Color.parseColor("#2BB673"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setCircleColor(Color.parseColor("#2BB673"));
        dataSet.setLineWidth(2f);

        lineChart.setData(new LineData(dataSet));
        lineChart.invalidate();
    }

    private String getStartDate(String type) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        switch (type) {
            case "Day":
                return sdf.format(calendar.getTime());
            case "Week":
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case "Month":
                calendar.add(Calendar.DAY_OF_YEAR, -30);
                break;
        }
        return sdf.format(calendar.getTime());
    }
}
