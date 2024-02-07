package com.example.project;

import static android.app.Activity.RESULT_OK;



import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import android.content.Context;
import java.io.InputStream;
import java.io.IOException;
import org.json.JSONObject;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private EditText etProfileFirstName, etProfileLastName, etProfileEmail, etRegPhoneNumber, etRegAreaCode;
    private ImageView ivProfileImage;
    private Button btnChangePassword, btnUpdateProfile, btnClearFavorites, btnClearReservations;
    private Spinner spinnerGender, spinnerCountry, spinnerCity;
    private HashMap<String, String> countryAreaCodes;
    private HashMap<String, ArrayList<String>> countriesCitiesMap;
    // Assuming you have a User model class
    private User currentUser;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private Uri imageUri;
    public ProfileFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        // Initialize views
        etProfileFirstName = view.findViewById(R.id.etProfileFirstName);
        etProfileLastName = view.findViewById(R.id.etProfileLastName);
        etProfileEmail = view.findViewById(R.id.etProfileEmail);
        etRegPhoneNumber = view.findViewById(R.id.etRegPhoneNumber);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);
        btnClearFavorites = view.findViewById(R.id.btnClearFavorites);
        btnClearReservations = view.findViewById(R.id.btnClearReservations);

        etRegAreaCode = view.findViewById(R.id.etRegAreaCode);
        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerCountry = view.findViewById(R.id.spinnerCountry);
        spinnerCity = view.findViewById(R.id.spinnerCity);
        populateGenderSpinner();
        loadJsonData();
        populateCountrySpinner();
        // Load user data
        loadUserData();

        btnChangePassword.setOnClickListener(v -> {
            // Create an instance of your ChangePasswordDialogFragment
            ChangePasswordDialogFragment changePasswordDialogFragment = new ChangePasswordDialogFragment();

            // Get the FragmentManager from the parent activity
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

            // Show the dialog fragment using the FragmentManager
            changePasswordDialogFragment.show(fragmentManager, "ChangePasswordDialogFragment");
        });


        btnUpdateProfile.setOnClickListener(v -> handleUpdateProfile());
        btnClearFavorites.setOnClickListener(v -> handleClearFavorites());
        btnClearReservations.setOnClickListener(v -> handleClearReservations());
        ivProfileImage.setOnClickListener(v -> selectImage());

        return view;
    }

    private void loadUserData() {
        String email = mAuth.getCurrentUser().getEmail();
        db.collection("users").document(email).get().addOnSuccessListener(documentSnapshot -> {
            currentUser = documentSnapshot.toObject(User.class);
            etProfileFirstName.setText(currentUser.getFirstName());
            etProfileLastName.setText(currentUser.getLastName());
            etProfileEmail.setText(currentUser.getEmail());
            etRegPhoneNumber.setText(currentUser.getPhone_number());
            setAreaCode(currentUser.getCountry());
            setUserCountryInSpinner(spinnerCountry, currentUser.getCountry());
            setUserCountryInSpinner(spinnerCity, currentUser.getCity());
            setUserCountryInSpinner(spinnerGender, currentUser.getGender());


            Glide.with(ivProfileImage.getContext())
                    .load(currentUser.getImageUrl())
                    .into(ivProfileImage);
            // Populate the fields with currentUser data
        });
    }
    private void setUserCountryInSpinner(Spinner spinner, String userCountry) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter(); // Cast to the specific adapter you are using
        if (adapter != null) {
            int position = adapter.getPosition(userCountry);
            if (position >= 0) {
                // Position found, set the spinner to this value
                spinner.setSelection(position);
            }
        }
    }



    private void handleUpdateProfile() {
        String newFirstName = etProfileFirstName.getText().toString().trim();
        String newLastName = etProfileLastName.getText().toString().trim();
        String newEmail = etProfileEmail.getText().toString().trim();
        String newPhoneNumber = etRegPhoneNumber.getText().toString().trim();
        String dialCode = etRegAreaCode.getText().toString();
        String Gender = spinnerGender.getSelectedItem().toString();
        String Country = spinnerCountry.getSelectedItem().toString();
        String City = spinnerCity.getSelectedItem().toString();

        Map<String, Object> updates = new HashMap<>();
        if (!Gender.isEmpty() && !Gender.equals(currentUser.getGender()) ) {
            updates.put("gender", Gender);
            currentUser.setGender(Gender); // Update currentUser object
        }
        if (!Country.isEmpty() && !Country.equals(currentUser.getCountry()) ) {
            updates.put("country", Country);
            currentUser.setCountry(Country); // Update currentUser object
        }
        if (!City.isEmpty() && !City.equals(currentUser.getCity()) ) {
            updates.put("city", City);
            currentUser.setCity(City); // Update currentUser object
        }
        if (!newFirstName.isEmpty() && !newFirstName.equals(currentUser.getFirstName()) ) {
            if (!Validation.isValidName(newFirstName) ) {
                Toast.makeText(getContext(), "First and last names must be at least 3 characters long", Toast.LENGTH_SHORT).show();
                return;
            }
            updates.put("firstName", newFirstName);
            currentUser.setFirstName(newFirstName); // Update currentUser object
        }
        if (!newLastName.isEmpty() && !newLastName.equals(currentUser.getLastName()) ) {
            if (!Validation.isValidName(newLastName) ) {
                Toast.makeText(getContext(), "First and last names must be at least 3 characters long", Toast.LENGTH_SHORT).show();
                return;
            }
            updates.put("lastName", newLastName);
            currentUser.setLastName(newLastName); // Update currentUser object
        }
        if (!newEmail.isEmpty() && Validation.isValidEmail(newEmail) && !newEmail.equals(currentUser.getEmail())) {
            if (!Validation.isValidEmail(newEmail)) {
                Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the email is already in use
            mAuth.fetchSignInMethodsForEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().getSignInMethods().isEmpty()) {
                        // Email not in use, proceed to update it in FirebaseAuth
                        updateEmailInAuth(newEmail, updates);
                    } else {
                        // Email already in use
                        Toast.makeText(getContext(), "Email is already in use.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    // Failed to check email
                    Toast.makeText(getContext(), "Failed to check email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("error",""+task.getException().getMessage());
                }
            });
        } else {
            // Email not being updated, update other fields
            updateFirestore(updates);
        }

        if (!newPhoneNumber.isEmpty() && !newPhoneNumber.equals(currentUser.getPhone_number()) ) {
            if (!Validation.isValidPhoneNumber(dialCode, newPhoneNumber)) {
                Toast.makeText(getContext(), "Invalid phone number format", Toast.LENGTH_SHORT).show();
                return;
            }
            updates.put("phone_Number", newPhoneNumber);
            currentUser.setPhone_number(newPhoneNumber); // Update currentUser object
        }

        if (!updates.isEmpty()) {
            db.collection("users").document(mAuth.getCurrentUser().getEmail()).update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        // Reload user data here if necessary
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "No changes to update", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmailInAuth(String newEmail, Map<String, Object> updates) {
        mAuth.getCurrentUser().updateEmail(newEmail).addOnCompleteListener(updateEmailTask -> {
            if (updateEmailTask.isSuccessful()) {
                // Email updated in FirebaseAuth, now update in Firestore
                updates.put("email", newEmail);
                currentUser.setEmail(newEmail);
                updateFirestore(updates);
            } else {
                // Failed to update email in FirebaseAuth
                Toast.makeText(getContext(), "Failed to update email: " + updateEmailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("error",""+updateEmailTask.getException().getMessage() );
            }
        });
    }

    private void updateFirestore(Map<String, Object> updates) {
        if (!updates.isEmpty()) {
            db.collection("users").document(mAuth.getCurrentUser().getEmail()).update(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "No changes to update", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleChangePassword() {
        // Implement password change logic
    }

    private void handleClearFavorites() {
        currentUser.setFavoriteCarIds(new ArrayList<>());
        db.collection("users").document(currentUser.getEmail()).update("favoriteCarIds", currentUser.getFavoriteCarIds())
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Favorites cleared successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to clear favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void handleClearReservations() {
        currentUser.setReservedCarIds(new ArrayList<>());
        db.collection("users").document(currentUser.getEmail()).update("reservedCarIds", currentUser.getReservedCarIds())
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Reservations cleared successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to clear reservations: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Existing image selection logic
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST || requestCode == REQUEST_IMAGE_CAPTURE) {
                imageUri = data.getData();
                ivProfileImage.setImageURI(imageUri);
                uploadImageToFirebaseStorage();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child("profileImages/" + UUID.randomUUID().toString());
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        db.collection("users").document(mAuth.getCurrentUser().getEmail()).update("imageUrl", imageUrl)
                                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Image updated successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                }
            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }









    private void populateGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }


    private void populateCountrySpinner() {
        ArrayList<String> countries = new ArrayList<>(countriesCitiesMap.keySet());
        Collections.sort(countries);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, countries);
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
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cities);
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
            InputStream is = getContext().getAssets().open("all_arab_countries_and_cities.json");
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






}
