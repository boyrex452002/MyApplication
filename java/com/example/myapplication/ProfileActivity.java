package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView textUsername, textFullName, textAge, textGender, textCountry, textBio, textEmail;
    private Button editProfileBtn, historyBtn, backToMainBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Link UI elements
        textUsername = findViewById(R.id.textUsername);
        textFullName = findViewById(R.id.textFullName);
        textAge = findViewById(R.id.textAge);
        textGender = findViewById(R.id.textGender);
        textCountry = findViewById(R.id.textCountry);
        textBio = findViewById(R.id.textBio);
        textEmail = findViewById(R.id.textEmail);

        editProfileBtn = findViewById(R.id.editProfileBtn);
        historyBtn = findViewById(R.id.historyBtn);
        backToMainBtn = findViewById(R.id.backToMainBtn);

        // Firebase setup
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display email
        textEmail.setText(user.getEmail());

        // Load profile data
        loadProfileData(user.getUid());

        // Edit Profile button click
        editProfileBtn.setOnClickListener(v -> showEditProfileDialog(user.getUid()));

        // History button click
        historyBtn.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, HistoryActivity.class)));

        // Back to Main button click
        backToMainBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadProfileData(String userId) {
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        textUsername.setText(documentSnapshot.getString("username"));
                        textFullName.setText(documentSnapshot.getString("fullName"));
                        textAge.setText(documentSnapshot.getString("age"));
                        textGender.setText(documentSnapshot.getString("gender"));
                        textCountry.setText(documentSnapshot.getString("country"));
                        textBio.setText(documentSnapshot.getString("bio"));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show());
    }

    private void showEditProfileDialog(String userId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_profile);
        dialog.setCancelable(true);

        // Set dialog window background to be opaque
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // Link dialog UI elements
        EditText editUsername = dialog.findViewById(R.id.editUsername);
        EditText editFullName = dialog.findViewById(R.id.editFullName);
        EditText editAge = dialog.findViewById(R.id.editAge);
        EditText editGender = dialog.findViewById(R.id.editGender);
        EditText editCountry = dialog.findViewById(R.id.editCountry);
        EditText editBio = dialog.findViewById(R.id.editBio);
        Button saveBtn = dialog.findViewById(R.id.saveBtn);
        Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

        // Load current data into edit fields
        firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editUsername.setText(documentSnapshot.getString("username"));
                        editFullName.setText(documentSnapshot.getString("fullName"));
                        editAge.setText(documentSnapshot.getString("age"));
                        editGender.setText(documentSnapshot.getString("gender"));
                        editCountry.setText(documentSnapshot.getString("country"));
                        editBio.setText(documentSnapshot.getString("bio"));
                    }
                });

        // Save button click
        saveBtn.setOnClickListener(v -> {
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", editUsername.getText().toString().trim());
            userData.put("fullName", editFullName.getText().toString().trim());
            userData.put("age", editAge.getText().toString().trim());
            userData.put("gender", editGender.getText().toString().trim());
            userData.put("country", editCountry.getText().toString().trim());
            userData.put("bio", editBio.getText().toString().trim());

            firestore.collection("users").document(userId)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                        loadProfileData(userId); // Refresh displayed data
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show());
        });

        // Cancel button click
        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}