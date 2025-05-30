package com.example.solarmonitorapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RealTimeEfficiencyActivity extends AppCompatActivity {

    EditText etDate, etEnergy, etEfficiency, etRecordId;
    Button btnSave, btnView, btnUpdate, btnDelete;
    TextView tvData, tvTotalEnergy, tvAvgEfficiency, tvMaxCloud, tvCurrentOutput, tvCurrentEfficiency;
    DatabaseHelper dbHelper;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_efficiency);

        // Initialize views
        etDate = findViewById(R.id.etDate);
        etEnergy = findViewById(R.id.etEnergy);
        etEfficiency = findViewById(R.id.etEfficiency);
        etRecordId = findViewById(R.id.etRecordId);
        btnSave = findViewById(R.id.btnSave);
        btnView = findViewById(R.id.btnView);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        tvData = findViewById(R.id.tvData);

        tvTotalEnergy = findViewById(R.id.tvTotalEnergy);
        tvAvgEfficiency = findViewById(R.id.tvAvgEfficiency);
        tvMaxCloud = findViewById(R.id.tvMaxCloud);
        tvCurrentOutput = findViewById(R.id.tvCurrentOutput);
        tvCurrentEfficiency = findViewById(R.id.tvCurrentEfficiency);

        dbHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(v -> {
            String date = etDate.getText().toString().trim();
            String energyStr = etEnergy.getText().toString().trim();
            String efficiencyStr = etEfficiency.getText().toString().trim();

            if (date.isEmpty() || energyStr.isEmpty() || efficiencyStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                float energy = Float.parseFloat(energyStr);
                float efficiency = Float.parseFloat(efficiencyStr);

                boolean inserted = dbHelper.insertEnergy(date, energy, efficiency);
                if (inserted) {
                    Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show();
                    etDate.setText("");
                    etEnergy.setText("");
                    etEfficiency.setText("");
                    updateRealTimeSummary();
                } else {
                    Toast.makeText(this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });

        btnView.setOnClickListener(v -> refreshDataDisplay());

        btnUpdate.setOnClickListener(v -> {
            String idStr = etRecordId.getText().toString().trim();

            if (idStr.isEmpty()) {
                Toast.makeText(this, "Enter Record ID to update", Toast.LENGTH_SHORT).show();
                return;
            }

            int id = Integer.parseInt(idStr);
            Cursor cursor = dbHelper.getEnergyById(id);

            if (cursor != null && cursor.moveToFirst()) {
                String currentDate = cursor.getString(1);
                String currentEnergy = String.valueOf(cursor.getFloat(2));
                String currentEfficiency = String.valueOf(cursor.getFloat(3));
                cursor.close();

                showUpdateDialog(id, currentDate, currentEnergy, currentEfficiency);
            } else {
                Toast.makeText(this, "Record not found.", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            String idStr = etRecordId.getText().toString().trim();

            if (idStr.isEmpty()) {
                Toast.makeText(this, "Enter Record ID to delete", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int id = Integer.parseInt(idStr);
                new AlertDialog.Builder(this)
                        .setTitle("Delete Confirmation")
                        .setMessage("Are you sure you want to delete the record?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            boolean deleted = dbHelper.deleteEnergy(id);
                            if (deleted) {
                                Toast.makeText(this, "Record deleted!", Toast.LENGTH_SHORT).show();
                                updateRealTimeSummary();
                                refreshDataDisplay();
                            } else {
                                Toast.makeText(this, "Delete failed. ID not found.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid ID format", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView imgBell = findViewById(R.id.imgBell);
        imgBell.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_efficiency);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                return true;
            } else if (id == R.id.nav_efficiency) {
                return true;
            } else if (id == R.id.nav_production) {
                startActivity(new Intent(getApplicationContext(), PredictionActivity.class));
                return true;
            } else if (id == R.id.nav_impact) {
                startActivity(new Intent(getApplicationContext(), EnvironmentalImpactActivity.class));
                return true;
            } else if (id == R.id.nav_reports) {
                startActivity(new Intent(getApplicationContext(), ReportActivity.class));
                return true;
            }
            return false;
        });

        updateRealTimeSummary(); // keep this to update summary
        // refreshDataDisplay(); // ← removed this to prevent auto-display of records
    }

    private void showUpdateDialog(int id, String currentDate, String currentEnergy, String currentEfficiency) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Record");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText etNewDate = new EditText(this);
        etNewDate.setHint("Date (YYYY-MM-DD)");
        etNewDate.setText(currentDate);
        layout.addView(etNewDate);

        EditText etNewEnergy = new EditText(this);
        etNewEnergy.setHint("Energy Produced (W)");
        etNewEnergy.setText(currentEnergy);
        layout.addView(etNewEnergy);

        EditText etNewEfficiency = new EditText(this);
        etNewEfficiency.setHint("Efficiency (%)");
        etNewEfficiency.setText(currentEfficiency);
        layout.addView(etNewEfficiency);

        builder.setView(layout);

        builder.setPositiveButton("Update", (dialog, which) -> {
            try {
                String newDate = etNewDate.getText().toString().trim();
                float newEnergy = Float.parseFloat(etNewEnergy.getText().toString().trim());
                float newEfficiency = Float.parseFloat(etNewEfficiency.getText().toString().trim());

                boolean updated = dbHelper.updateEnergy(id, newDate, newEnergy, newEfficiency);
                if (updated) {
                    Toast.makeText(this, "Record updated!", Toast.LENGTH_SHORT).show();
                    updateRealTimeSummary();
                    refreshDataDisplay();
                } else {
                    Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Invalid input. Please check your values.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateRealTimeSummary() {
        Cursor cursor = dbHelper.getAllEnergyData();
        float totalEnergy = 0;
        float totalEfficiency = 0;
        int count = 0;

        while (cursor.moveToNext()) {
            totalEnergy += cursor.getFloat(2);
            totalEfficiency += cursor.getFloat(3);
            count++;
        }

        cursor.close();

        float avgEfficiency = count > 0 ? totalEfficiency / count : 0;

        tvTotalEnergy.setText(String.format("🔋 %.1f kWh generated", totalEnergy));
        tvAvgEfficiency.setText(String.format("🌡️ Avg Efficiency: %.2f %%", avgEfficiency));
        tvMaxCloud.setText("☁ Max Cloud: Medium");

        tvCurrentOutput.setText("Output\n" + (count > 0 ? (totalEnergy / count) + " kW" : "0 kW"));
        tvCurrentEfficiency.setText("Efficiency\n" + (int) avgEfficiency + "%");
    }

    private void refreshDataDisplay() {
        Cursor res = dbHelper.getAllEnergyData();
        if (res.getCount() == 0) {
            tvData.setText("No data found.");
            return;
        }

        StringBuilder buffer = new StringBuilder();
        while (res.moveToNext()) {
            buffer.append("ID: ").append(res.getString(0)).append("\n");
            buffer.append("Date: ").append(res.getString(1)).append("\n");
            buffer.append("Energy: ").append(res.getString(2)).append(" W\n");
            buffer.append("Efficiency: ").append(res.getString(3)).append(" %\n\n");
        }

        tvData.setText(buffer.toString());
    }
}
