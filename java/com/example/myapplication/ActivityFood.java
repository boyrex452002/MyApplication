package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityFood extends AppCompatActivity {

    private Spinner spinnerDietType, spinnerWeeklyConsumption, spinnerFoodConsumptionType;
    private Button calculateBtn;

    private CarbonFootprintData footprintData;

    // Multipliers for food footprint (kg CO2 per week)
    private final double MEAT_HEAVY = 20.0;
    private final double BALANCED = 15.0;
    private final double VEGETARIAN = 10.0;
    private final double VEGAN = 5.0;

    private final double HIGH_CONSUMPTION = 1.2;
    private final double MEDIUM_CONSUMPTION = 1.0;
    private final double LOW_CONSUMPTION = 0.8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        spinnerDietType = findViewById(R.id.spinner_vehicle);
        spinnerWeeklyConsumption = findViewById(R.id.spinner_Fuel);
        spinnerFoodConsumptionType = findViewById(R.id.spin_eat);
        calculateBtn = findViewById(R.id.button2);

        footprintData = (CarbonFootprintData) getIntent().getSerializableExtra("footprintData");
        if (footprintData == null) footprintData = new CarbonFootprintData();

        ArrayAdapter<CharSequence> dietAdapter = ArrayAdapter.createFromResource(this,
                R.array.diet_types, android.R.layout.simple_spinner_item);
        dietAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDietType.setAdapter(dietAdapter);

        ArrayAdapter<CharSequence> consumptionAdapter = ArrayAdapter.createFromResource(this,
                R.array.food_consumption_types, android.R.layout.simple_spinner_item);
        consumptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeeklyConsumption.setAdapter(consumptionAdapter);
        spinnerFoodConsumptionType.setAdapter(consumptionAdapter);

        calculateBtn.setOnClickListener(v -> {
            String dietType = spinnerDietType.getSelectedItem().toString();
            String weeklyConsumption = spinnerWeeklyConsumption.getSelectedItem().toString();
            String foodConsumptionType = spinnerFoodConsumptionType.getSelectedItem().toString();

            double baseEmission = getDietMultiplier(dietType);
            double weeklyMultiplier = getConsumptionMultiplier(weeklyConsumption);
            double foodTypeMultiplier = getConsumptionMultiplier(foodConsumptionType);

            double foodEmission = baseEmission * weeklyMultiplier * foodTypeMultiplier;

            footprintData.setFoodEmission(foodEmission);
            footprintData.calculateTotalEmission();

            Intent intent = new Intent(ActivityFood.this, ResultInterface.class);
            intent.putExtra("footprintData", footprintData);
            startActivity(intent);
            finish();
        });
    }

    private double getDietMultiplier(String diet) {
        switch (diet) {
            case "Meat-heavy": return MEAT_HEAVY;
            case "Balanced": return BALANCED;
            case "Vegetarian": return VEGETARIAN;
            case "Vegan": return VEGAN;
            default: return BALANCED;
        }
    }

    private double getConsumptionMultiplier(String consumption) {
        switch (consumption) {
            case "High": return HIGH_CONSUMPTION;
            case "Medium": return MEDIUM_CONSUMPTION;
            case "Low": return LOW_CONSUMPTION;
            default: return MEDIUM_CONSUMPTION;
        }
    }
}
