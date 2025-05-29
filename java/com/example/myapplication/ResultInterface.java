package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ResultInterface extends AppCompatActivity {

    private TextView totalEmissionText;
    private Button indicatorBtn, tipsBtn, homeBtn;
    private CarbonFootprintData footprintData;

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_interface);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        totalEmissionText = findViewById(R.id.totalemission);
        indicatorBtn = findViewById(R.id.buttonindicator);
        tipsBtn = findViewById(R.id.Tips_btn);
        homeBtn = findViewById(R.id.home);

        footprintData = (CarbonFootprintData) getIntent().getSerializableExtra("footprintData");
        if (footprintData == null) {
            Toast.makeText(this, "No data to display!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        double totalEmission = footprintData.getTotalEmission();

        totalEmissionText.setText(String.format("%.2f", totalEmission));
        applyEmissionIndicator(totalEmission);

        saveEmissionToFirestore(totalEmission);

        homeBtn.setOnClickListener(v -> {
            startActivity(new Intent(ResultInterface.this, MainActivity.class));
            finish();
        });

        tipsBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Tips feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void applyEmissionIndicator(double emission) {
        if (emission > 1000) {
            indicatorBtn.setText("High");
            indicatorBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorHigh));
        } else if (emission > 500) {
            indicatorBtn.setText("Moderate");
            indicatorBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorModerate));
        } else {
            indicatorBtn.setText("Low");
            indicatorBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorLow));
        }
    }

    private void saveEmissionToFirestore(double totalEmission) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in. Cannot save data.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("transportationEmission", footprintData.getTransportationEmission());
        data.put("energyEmission", footprintData.getEnergyEmission());
        data.put("shoppingEmission", footprintData.getShoppingEmission());
        data.put("foodEmission", footprintData.getFoodEmission());
        data.put("totalEmission", totalEmission);
        data.put("timestamp", Timestamp.now());

        firestore.collection("users")
                .document(userId)
                .collection("history")
                .add(data)
                .addOnSuccessListener(docRef ->
                        Toast.makeText(this, "Calculation saved to history.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
