package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryDetailActivity extends AppCompatActivity {

    private TextView textDateTime, textTotalEmission, textTransportation, textShopping, textEnergy, textFood;
    private Button buttonEditTimestamp, buttonDelete, indicatorBtn, closeBtn;

    private FirebaseFirestore db;
    private HistoryItem currentHistoryItem;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy 'at' HH:mm:ss", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        textDateTime = findViewById(R.id.textDateTime);
        textTotalEmission = findViewById(R.id.textTotalEmission);
        textTransportation = findViewById(R.id.textTransportation);
        textShopping = findViewById(R.id.textShopping);
        textEnergy = findViewById(R.id.textEnergy);
        textFood = findViewById(R.id.textFood);
        buttonEditTimestamp = findViewById(R.id.buttonEditTimestamp);
        buttonDelete = findViewById(R.id.buttonDelete);
        indicatorBtn = findViewById(R.id.buttonindicator);
        closeBtn = findViewById(R.id.closeBtn);

        db = FirebaseFirestore.getInstance();

        String historyId = getIntent().getStringExtra("historyId");
        if (historyId == null) {
            Toast.makeText(this, "No history ID provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadHistoryItem(historyId);

        buttonEditTimestamp.setOnClickListener(v -> showEditTimestampDialog());

        buttonDelete.setOnClickListener(v -> deleteHistory());

        closeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HistoryDetailActivity.this, HistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadHistoryItem(String historyId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .collection("history")
                .document(historyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentHistoryItem = documentSnapshot.toObject(HistoryItem.class);
                        if (currentHistoryItem != null) {
                            currentHistoryItem.setId(historyId);
                            displayData();
                        } else {
                            Toast.makeText(this, "Failed to load history data", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "History record not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading history: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void displayData() {
        String formattedDate = "";
        Timestamp ts = currentHistoryItem.getTimestamp();
        if (ts != null) {
            Date date = ts.toDate();
            formattedDate = sdf.format(date);
        }
        textDateTime.setText(formattedDate);

        textTotalEmission.setText(String.format(Locale.getDefault(), "Total Emissions: %.2f kg CO₂", currentHistoryItem.getTotalEmission()));

        textTransportation.setText(String.format(Locale.getDefault(),
                "Transportation Emission: %.2f kg CO₂ (%s)",
                currentHistoryItem.getTransportationEmission(),
                currentHistoryItem.getTransportation() != null ? currentHistoryItem.getTransportation() : "N/A"));

        textShopping.setText(String.format(Locale.getDefault(),
                "Shopping Emission: %.2f kg CO₂ (%s)",
                currentHistoryItem.getShoppingEmission(),
                currentHistoryItem.getShopping() != null ? currentHistoryItem.getShopping() : "N/A"));

        textEnergy.setText(String.format(Locale.getDefault(),
                "Energy Emission: %.2f kg CO₂ (%s)",
                currentHistoryItem.getEnergyEmission(),
                currentHistoryItem.getEnergy() != null ? currentHistoryItem.getEnergy() : "N/A"));

        textFood.setText(String.format(Locale.getDefault(),
                "Food Emission: %.2f kg CO₂ (%s)",
                currentHistoryItem.getFoodEmission(),
                currentHistoryItem.getFood() != null ? currentHistoryItem.getFood() : "N/A"));

        applyEmissionIndicator(currentHistoryItem.getTotalEmission());
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
        indicatorBtn.setTextSize(30f);
    }

    private void showEditTimestampDialog() {
        final EditText input = new EditText(this);
        input.setText(textDateTime.getText());

        new AlertDialog.Builder(this)
                .setTitle("Edit Timestamp")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newTimestamp = input.getText().toString().trim();
                    if (!newTimestamp.isEmpty()) {
                        updateTimestamp(newTimestamp);
                    } else {
                        Toast.makeText(this, "Timestamp cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTimestamp(String newTimestampStr) {
        try {
            // Parse the user input string into a Date object
            Date newDate = sdf.parse(newTimestampStr);
            if (newDate == null) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert Date to Firebase Timestamp
            Timestamp newTimestamp = new Timestamp(newDate);

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            db.collection("users").document(userId)
                    .collection("history")
                    .document(currentHistoryItem.getId())
                    .update("timestamp", newTimestamp)
                    .addOnSuccessListener(aVoid -> {
                        // Update local model and UI
                        currentHistoryItem.setTimestamp(newTimestamp);
                        textDateTime.setText(newTimestampStr);
                        Toast.makeText(this, "Timestamp updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format, please use: " + sdf.toPattern(), Toast.LENGTH_LONG).show();
        }
    }


    private void deleteHistory() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("history")
                .document(currentHistoryItem.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "History deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
