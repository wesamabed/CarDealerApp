package com.example.project;

import static com.example.project.Validation.generateSessionToken;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private CheckBox checkBoxRememberMe;
    private Button btnLogin, btnToRegister, btnToRegisterCarDealer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxRememberMe = findViewById(R.id.checkboxRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        btnToRegister = findViewById(R.id.btnToRegister);
        btnToRegisterCarDealer = findViewById(R.id.btnToRegisterCarDealer);
        btnLogin.setOnClickListener(view -> {
            performLogin();
            String userEmail = editTextEmail.getText().toString().trim();
            String sessionToken = generateSessionToken(userEmail);
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("session_token", sessionToken);
            editor.apply();
        });

        btnToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        btnToRegisterCarDealer.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, CarDealerRegistrationActivity.class);
            startActivity(intent);
        });

        // Load remembered email if available
        loadRememberedEmail();
    }

    private void loadRememberedEmail() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String rememberedEmail = preferences.getString("email", "");
        if (!rememberedEmail.isEmpty()) {
            editTextEmail.setText(rememberedEmail);
            checkBoxRememberMe.setChecked(true);
        }
    }

    private void performLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        // Check if email or password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email and password are required.", Toast.LENGTH_SHORT).show();
            return; // Return early to prevent further execution
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        handleRememberMeOption(email); // Handle remember me option

                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            fetchUserData(currentUser.getEmail());
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to retrieve user information.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleRememberMeOption(String email) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (checkBoxRememberMe.isChecked()) {
            editor.putString("email", email);
        } else {
            editor.remove("email");
        }
        editor.apply();
    }

    private void fetchUserData(String email) {
        db.collection("users").document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        if (user.isAdmin()) {
                            //startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                        } else if (user.isCarDealer()) {
                            startActivity(new Intent(LoginActivity.this, CarDealerDashboardActivity.class));

                        } else {
                            Intent intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        }
                    } else {
                        checkCarDealersCollection(email);
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Here we can also check the carDealers collection as a fallback
                    checkCarDealersCollection(email);
                });
    }

    private void checkCarDealersCollection(String email) {
        db.collection("carDealers").document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    CarDealer carDealer = documentSnapshot.toObject(CarDealer.class);
                    if (carDealer != null) {
                        startActivity(new Intent(LoginActivity.this, CarDealerDashboardActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "User not found in car dealers.", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Failed to retrieve car dealer data.", Toast.LENGTH_SHORT).show());
    }




}
