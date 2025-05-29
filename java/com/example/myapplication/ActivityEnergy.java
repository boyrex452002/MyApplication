package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class ActivityEnergy extends AppCompatActivity {

    private Spinner spinnerEnergySource;
    private EditText electricityUsageEditText;
    private Button nextButton;

    private CarbonFootprintData footprintData;
    private Map<String, Double> energyMultipliers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy);

        footprintData = (CarbonFootprintData) getIntent().getSerializableExtra("footprintData");
        if (footprintData == null) footprintData = new CarbonFootprintData();

        spinnerEnergySource = findViewById(R.id.spinner_Fuel);
        electricityUsageEditText = findViewById(R.id.distance_text);
        nextButton = findViewById(R.id.button4);

        setupSpinner(R.id.spinner_Fuel, R.array.energy_source_types);
        setupEnergyMultipliers();

        nextButton.setOnClickListener(v -> {
            String energySource = spinnerEnergySource.getSelectedItem().toString();
            String usageStr = electricityUsageEditText.getText().toString().trim();

            if (usageStr.isEmpty()) {
                Toast.makeText(this, "Please enter electricity usage", Toast.LENGTH_SHORT).show();
                return;
            }

            double usage;
            try {
                usage = Double.parseDouble(usageStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid electricity usage input", Toast.LENGTH_SHORT).show();
                return;
            }

            Double multiplier = energyMultipliers.get(energySource);
            if (multiplier == null) multiplier = 0.5; // fallback

            double emission = usage * multiplier;
            footprintData.setEnergyEmission(emission);
            footprintData.calculateTotalEmission();

            Toast.makeText(this, "Energy emissions saved", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ActivityEnergy.this, ActivityShopping.class);
            intent.putExtra("footprintData", footprintData);
            startActivity(intent);
            finish();
        });
    }

    private void setupSpinner(int spinnerId, int arrayResourceId) {
        Spinner spinner = findViewById(spinnerId);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                arrayResourceId,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupEnergyMultipliers() {
        energyMultipliers.put("Grid Electricity", 0.5);
        energyMultipliers.put("Solar", 0.1);
        energyMultipliers.put("Wind", 0.05);
        energyMultipliers.put("Hydro", 0.02);
    }
}
