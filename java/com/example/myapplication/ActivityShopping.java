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

public class ActivityShopping extends AppCompatActivity {

    private Spinner spinnerPurchaseType;
    private EditText amountSpentEditText;
    private Button nextButton;

    private CarbonFootprintData footprintData;
    private Map<String, Double> purchaseMultipliers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        footprintData = (CarbonFootprintData) getIntent().getSerializableExtra("footprintData");
        if (footprintData == null) footprintData = new CarbonFootprintData();

        spinnerPurchaseType = findViewById(R.id.spinner_Fuel);
        amountSpentEditText = findViewById(R.id.distance_text);
        nextButton = findViewById(R.id.button4);

        setupSpinner(R.id.spinner_Fuel, R.array.purchase_types);
        setupPurchaseMultipliers();

        nextButton.setOnClickListener(v -> {
            String purchaseType = spinnerPurchaseType.getSelectedItem().toString();
            String amountStr = amountSpentEditText.getText().toString().trim();

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter amount spent", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount input", Toast.LENGTH_SHORT).show();
                return;
            }

            Double multiplier = purchaseMultipliers.get(purchaseType);
            if (multiplier == null) multiplier = 0.2;

            double emission = amount * multiplier;
            footprintData.setShoppingEmission(emission);
            footprintData.calculateTotalEmission();

            Toast.makeText(this, "Shopping emissions saved", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ActivityShopping.this, ActivityFood.class);
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

    private void setupPurchaseMultipliers() {
        purchaseMultipliers.put("Clothing", 0.3);
        purchaseMultipliers.put("Electronics", 0.4);
        purchaseMultipliers.put("Groceries", 0.2);
        purchaseMultipliers.put("Luxury", 0.6);
    }
}
