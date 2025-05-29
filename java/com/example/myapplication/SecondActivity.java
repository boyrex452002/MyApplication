package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private Spinner spinnerVehicle, spinnerFuel;
    private EditText distanceText;
    private Button nextBtn;

    private CarbonFootprintData footprintData;

    // Multipliers
    private final double CAR_PETROL_MULTIPLIER = 0.2;
    private final double CAR_DIESEL_MULTIPLIER = 0.25;
    private final double CAR_ELECTRIC_MULTIPLIER = 0.05;
    private final double CAR_HYBRID_MULTIPLIER = 0.1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        spinnerVehicle = findViewById(R.id.spinner_vehicle);
        spinnerFuel = findViewById(R.id.spinner_Fuel);
        distanceText = findViewById(R.id.distance_text);
        nextBtn = findViewById(R.id.button4);

        footprintData = (CarbonFootprintData) getIntent().getSerializableExtra("footprintData");
        if (footprintData == null) footprintData = new CarbonFootprintData();

        ArrayAdapter<CharSequence> vehicleAdapter = ArrayAdapter.createFromResource(this,
                R.array.vehicle_types, android.R.layout.simple_spinner_item);
        vehicleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVehicle.setAdapter(vehicleAdapter);

        ArrayAdapter<CharSequence> fuelAdapter = ArrayAdapter.createFromResource(this,
                R.array.fuel_types, android.R.layout.simple_spinner_item);
        fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuel.setAdapter(fuelAdapter);

        nextBtn.setOnClickListener(v -> {
            try {
                double distance = Double.parseDouble(distanceText.getText().toString().trim());
                String fuel = spinnerFuel.getSelectedItem().toString();

                double multiplier = getFuelMultiplier(fuel);
                double transportationEmission = distance * multiplier;

                footprintData.setTransportationEmission(transportationEmission);
                footprintData.calculateTotalEmission();

                Intent intent = new Intent(SecondActivity.this, ActivityEnergy.class);
                intent.putExtra("footprintData", footprintData);
                startActivity(intent);
                finish();

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid distance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double getFuelMultiplier(String fuelType) {
        switch (fuelType) {
            case "Petrol": return CAR_PETROL_MULTIPLIER;
            case "Diesel": return CAR_DIESEL_MULTIPLIER;
            case "Electric": return CAR_ELECTRIC_MULTIPLIER;
            case "Hybrid": return CAR_HYBRID_MULTIPLIER;
            default: return 0.2;
        }
    }
}
