package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private TextView welcomeText;
    private Button addActivityBtn, loginBtn, logoutBtn, profileBtn, Tips_btn;

    // Emission TextViews
    private TextView currentTotalEmissionsText;
    private TextView energyEmissionText;
    private TextView transportationEmissionText;
    private TextView foodEmissionText;
    private TextView shoppingEmissionText;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFirebase();
        initializeViews();
        setupButtonListeners();
        checkUserAuthentication();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        addActivityBtn = findViewById(R.id.addActBtn);
        loginBtn = findViewById(R.id.login_btn);
        logoutBtn = findViewById(R.id.logoutBtn);
        profileBtn = findViewById(R.id.User_btn);
        Tips_btn = findViewById(R.id.Tips_btn);

        currentTotalEmissionsText = findViewById(R.id.currentTotalEmissions);
        energyEmissionText = findViewById(R.id.energyEmission);
        transportationEmissionText = findViewById(R.id.transportationEmission);
        foodEmissionText = findViewById(R.id.foodEmission);
        shoppingEmissionText = findViewById(R.id.shoppingEmission);
    }

    private void setupButtonListeners() {
        profileBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        logoutBtn.setOnClickListener(v -> logoutUser());

        addActivityBtn.setOnClickListener(v -> {
            // Start the flow by passing a new CarbonFootprintData object
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            intent.putExtra("footprintData", new CarbonFootprintData());
            startActivity(intent);
        });

        loginBtn.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else {
                showToast("You're already logged in!");
            }
        });

        Tips_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TipsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkUserAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            fetchUsernameFromFirestore(currentUser.getUid());
            fetchLatestEmissions(currentUser.getUid());
        } else {
            updateWelcomeMessage("Guest");

            clearEmissionTexts();
        }
    }

    private void fetchUsernameFromFirestore(String userId) {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        updateWelcomeMessage(username != null ? username : "User");
                    } else {
                        updateWelcomeMessage("User");
                        Log.w("Firestore", "Document does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    updateWelcomeMessage("User");
                    showToast("Failed to load username");
                    Log.e("Firestore", "Error fetching document", e);
                });
    }

    private void fetchLatestEmissions(String userId) {
        firestore.collection("users").document(userId)
                .collection("history")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        com.google.firebase.firestore.DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        Double totalEmission = document.getDouble("totalEmission");
                        Double energyEmission = document.getDouble("energyEmission");
                        Double transportationEmission = document.getDouble("transportationEmission");
                        Double foodEmission = document.getDouble("foodEmission");
                        Double shoppingEmission = document.getDouble("shoppingEmission");

                        currentTotalEmissionsText.setText(String.format("Total: %.2f kg CO₂", totalEmission != null ? totalEmission : 0));
                        energyEmissionText.setText(String.format("● Energy (%.2f kg)", energyEmission != null ? energyEmission : 0));
                        transportationEmissionText.setText(String.format("● Transportation (%.2f kg)", transportationEmission != null ? transportationEmission : 0));
                        foodEmissionText.setText(String.format("● Food (%.2f kg)", foodEmission != null ? foodEmission : 0));
                        shoppingEmissionText.setText(String.format("● Shopping (%.2f kg)", shoppingEmission != null ? shoppingEmission : 0));
                    } else {
                        clearEmissionTexts();
                    }
                })
                .addOnFailureListener(e -> {
                    clearEmissionTexts();
                    Toast.makeText(MainActivity.this, "Failed to load emissions", Toast.LENGTH_SHORT).show();
                });
    }

    private void clearEmissionTexts() {
        currentTotalEmissionsText.setText("Total: 0 kg CO₂");
        energyEmissionText.setText("● Energy (0 kg)");
        transportationEmissionText.setText("● Transportation (0 kg)");
        foodEmissionText.setText("● Food (0 kg)");
        shoppingEmissionText.setText("● Shopping (0 kg)");
    }

    private void updateWelcomeMessage(String name) {
        welcomeText.setText(String.format("Welcome %s!", name));
        updateUiBasedOnAuthState(mAuth.getCurrentUser() != null);
    }

    private void updateUiBasedOnAuthState(boolean isLoggedIn) {
        loginBtn.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        logoutBtn.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserAuthentication(); // Refresh user info every time activity resumes
    }
}
