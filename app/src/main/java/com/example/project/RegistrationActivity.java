package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistrationActivity extends AppCompatActivity {
    private HashMap<String, String> countryAreaCodes;
    private HashMap<String, ArrayList<String>> countriesCitiesMap;
    private EditText etRegEmail, etRegFirstName, etRegLastName, etRegPassword, etRegConfirmPassword, etRegPhoneNumber, etRegAreaCode;
    private Spinner spinnerGender, spinnerCountry, spinnerCity;
    private Button btnRegister, btnToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //DatabaseHelper dbHelper = new DatabaseHelper(this);
        //dbHelper.resetUsersTable();
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        spinnerCity = findViewById(R.id.spinnerCity);
        etRegAreaCode = findViewById(R.id.etRegAreaCode);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegFirstName = findViewById(R.id.etRegFirstName);
        etRegLastName = findViewById(R.id.etRegLastName);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        etRegPhoneNumber = findViewById(R.id.etRegPhoneNumber);
        btnRegister = findViewById(R.id.btnRegister);
        btnToLogin = findViewById(R.id.btnToLogin);
        populateGenderSpinner();
        loadJsonData();
        populateCountrySpinner();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String firstName = etRegFirstName.getText().toString().trim();
        String lastName = etRegLastName.getText().toString().trim();
        String email = etRegEmail.getText().toString().trim();
        String password = etRegPassword.getText().toString();
        String confirmPassword = etRegConfirmPassword.getText().toString();
        String dialCode = etRegAreaCode.getText().toString();
        String phoneNumber = etRegPhoneNumber.getText().toString();
        String Gender = spinnerGender.getSelectedItem().toString();
        String Country = spinnerCountry.getSelectedItem().toString();
        String City = spinnerCity.getSelectedItem().toString();


        // Validate Name
        if (!isValidName(firstName) || !isValidName(lastName)) {
            Toast.makeText(this, "First and last names must be at least 3 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Email
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Password
        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password does not meet the criteria", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if Passwords Match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Phone Number
        if (!isValidPhoneNumber(dialCode, phoneNumber)) {
            Toast.makeText(this, "Invalid phone number format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash Password
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            Toast.makeText(this, "Error in password processing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save User Data to Database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        if (dbHelper.checkEmailExists(email)) {
            Toast.makeText(this, "Email already in use", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean isAdded = dbHelper.addUser(new User(firstName, lastName, email, hashedPassword, phoneNumber, Gender, Country, City));
        if (isAdded) {
            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
            dbHelper.printAllUsersToLogcat(); // Call this method to print all users
        } else {
            Toast.makeText(this, "User registration failed or email already exists", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidName(String name) {
        return name != null && name.length() >= 3;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{5,}$";
        return password.matches(passwordPattern);
    }


    private String getCountryIsoCode(String dialCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        for (String region : phoneUtil.getSupportedRegions()) {
            int code = phoneUtil.getCountryCodeForRegion(region);
            Log.d("TAG","+"+code);
            if (dialCode.equals("+" + code)) {
                Log.d("TAG","+"+region);
                return region;
            }
        }
        return null;
    }

    private boolean isValidPhoneNumber(String dialCode, String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            String countryCode = getCountryIsoCode(dialCode);
            if (countryCode == null) {
                Log.e("PhoneValidation", "Invalid dial code: " + dialCode);
                return false;
            }
            PhoneNumber numberProto = phoneUtil.parse(phoneNumber, countryCode);
            return phoneUtil.isValidNumber(numberProto);
        } catch (NumberParseException e) {
            Log.e("NumberParseException", "Error: " + e.toString());
            return false;
        }
    }



    private void populateGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }


    private void populateCountrySpinner() {
        ArrayList<String> countries = new ArrayList<>(countriesCitiesMap.keySet());
        Collections.sort(countries);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = (String) parent.getItemAtPosition(position);
                populateCitySpinner(selectedCountry);
                setAreaCode(selectedCountry);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void populateCitySpinner(String country) {
        ArrayList<String> cities = countriesCitiesMap.get(country);
        if (cities != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCity.setAdapter(adapter);
        }
    }

    private void setAreaCode(String country) {
        String areaCode = countryAreaCodes.get(country);
        if (areaCode != null) {
            etRegAreaCode.setText(areaCode);
        }
    }

    private void loadJsonData() {
        countryAreaCodes = new HashMap<>();
        countriesCitiesMap = new HashMap<>();

        try {
            InputStream is = getAssets().open("all_arab_countries_and_cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(json);

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject countryInfo = jsonObject.getJSONObject(key);
                String areaCode = countryInfo.getString("area_code");
                countryAreaCodes.put(key, areaCode);

                ArrayList<String> cities = new ArrayList<>();
                JSONArray jsonArray = countryInfo.getJSONArray("cities");
                for (int i = 0; i < jsonArray.length(); i++) {
                    cities.add(jsonArray.getString(i));
                }
                countriesCitiesMap.put(key, cities);
                Log.d("LoadData", "Country: " + key + ", Area Code: " + areaCode + ", Cities: " + cities);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    // Additional methods for validation and database interactions
}
