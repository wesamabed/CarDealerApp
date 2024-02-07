package com.example.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class AddAdminFragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private HashMap<String, String> countryAreaCodes;
    private HashMap<String, ArrayList<String>> countriesCitiesMap;
    private EditText etRegEmail, etRegFirstName, etRegLastName, etRegPassword, etRegConfirmPassword, etRegPhoneNumber, etRegAreaCode;
    private Spinner spinnerGender, spinnerCountry, spinnerCity;
    private Button btnRegister, btnToLogin;
    private ImageView ivSelectedPhoto;
    private Button btnSelectOrCapturePhoto;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Uri imageUri;
    private StorageReference storageReference;




    public AddAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_add_admin, container, false);

        //requestPermissions();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        spinnerGender = view.findViewById(R.id.spinnerGender);
        spinnerCountry = view.findViewById(R.id.spinnerCountry);
        spinnerCity = view.findViewById(R.id.spinnerCity);
        etRegAreaCode = view.findViewById(R.id.etRegAreaCode);
        etRegEmail = view.findViewById(R.id.etRegEmail);
        etRegFirstName = view.findViewById(R.id.etRegFirstName);
        etRegLastName = view.findViewById(R.id.etRegLastName);
        etRegPassword = view.findViewById(R.id.etRegPassword);
        etRegConfirmPassword = view.findViewById(R.id.etRegConfirmPassword);
        etRegPhoneNumber = view.findViewById(R.id.etRegPhoneNumber);
        btnRegister = view.findViewById(R.id.btnRegister);
        btnToLogin = view.findViewById(R.id.btnToLogin);
        ivSelectedPhoto = view.findViewById(R.id.ivSelectedPhoto);
        btnSelectOrCapturePhoto = view.findViewById(R.id.btnSelectOrCapturePhoto);
        storageReference = FirebaseStorage.getInstance().getReference();
        btnSelectOrCapturePhoto.setOnClickListener(v -> {
            // Correctly reference 'view' from the surrounding context
            selectImage();
        });

        etRegPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                // Correctly reference 'view' from the surrounding context
                updatePasswordStrengthView(editable.toString(), view);
            }
        });
        populateGenderSpinner();
        loadJsonData();
        populateCountrySpinner();
        btnRegister.setOnClickListener(v -> {
            // Correctly reference 'view' from the surrounding context
            registerCarDealer();
        });


        btnToLogin.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });


        return view;
    }

    private void selectImage() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Add Photo!");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
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
                }
            });
            builder.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                ivSelectedPhoto.setImageURI(imageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ivSelectedPhoto.setImageBitmap(imageBitmap);
                // Save the bitmap to a file and get its URI
                imageUri = getImageUri(getActivity().getApplicationContext(), imageBitmap);
            }
        }
    }



    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private void uploadImageAndRegister(CarDealer CarDealer) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child("images/" + UUID.randomUUID().toString());
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        CarDealer.setImageUrl(imageUrl);
                        registerCarDealerInFirestore(CarDealer);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            registerCarDealerInFirestore(CarDealer);
        }
    }

    private void registerCarDealerInFirestore(CarDealer CarDealer) {
        db.collection("carDealers").document(CarDealer.getEmail()).set(CarDealer)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "CarDealer registered successfully", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getContext(), LoginActivity.class));
                    mAuth.signOut();
                    //finish();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save CarDealer details: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }


    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void registerCarDealer() {
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("carDealers").document(currentUser.getEmail()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        CarDealer carDealer = documentSnapshot.toObject(CarDealer.class);
                        if (carDealer != null) {
                            db.collection("companies").document(carDealer.getCarDealerId()).get()
                                    .addOnSuccessListener(documentSnapshotCompany -> {
                                        Company company = documentSnapshotCompany.toObject(Company.class);
                                        if (company != null) {
                                            String companyID = company.getcarDealerId();
                                            String locations = company.getLocation();
                                            continueRegistration(firstName, lastName, email, password, confirmPassword, dialCode, phoneNumber, Gender, Country, City, companyID, locations);
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("ProfileFragment", "Error loading company data", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("ProfileFragment", "Error loading car dealer data", e));
        }
    }



    private void continueRegistration(String firstName, String lastName, String email, String password, String confirmPassword, String dialCode, String phoneNumber, String Gender, String Country, String City, String companyID, String locations) {

        // Validate Name
        if (!Validation.isValidName(firstName) || !Validation.isValidName(lastName)) {
            Toast.makeText(getContext(), "First and last names must be at least 3 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Email
        if (!Validation.isValidEmail(email)) {
            Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Password
        if (!Validation.isValidPassword(password)) {
            Toast.makeText(getContext(), "Password does not meet the criteria", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if Passwords Match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate Phone Number
        if (!Validation.isValidPhoneNumber(dialCode, phoneNumber)) {
            Toast.makeText(getContext(), "Invalid phone number format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash Password
        String hashedPassword = Validation.hashPassword(password);
        if (hashedPassword == null) {
            Toast.makeText(getContext(), "Error in password processing", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> signInMethods = task.getResult().getSignInMethods();
                if (signInMethods != null && !signInMethods.isEmpty()) {
                    // Email already exists
                    Toast.makeText(getContext(), "Email already in use. Please use a different email.", Toast.LENGTH_SHORT).show();
                } else {
                    // Email is not in use, proceed with creating the user
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(createUserTask -> {
                                if (createUserTask.isSuccessful()) {
                                    // CarDealer registration successful, update Firestore with additional details
                                    List<String> admin = new ArrayList<>();
                                    admin.add(0,email);
                                    CarDealer newCarDealer = new CarDealer(firstName, lastName, email, companyID, phoneNumber, Gender, Country, City, locations, "", false, true, false);
                                    uploadImageAndRegister(newCarDealer);
                                } else {
                                    // Handle registration failure
                                    if (createUserTask.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(getContext(), "Email already in use. Please use a different email.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Registration failed: " + createUserTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            } else {
                // Handle error in fetchSignInMethodsForEmail
                Toast.makeText(getContext(), "Failed to check email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
            InputStream is = getActivity().getAssets().open("all_arab_countries_and_cities.json");
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


    private void updatePasswordStrengthView(String password, View view) {
        TextView passwordStrengthView = view.findViewById(R.id.tvPasswordStrength);

        if (TextUtils.isEmpty(password)) {
            passwordStrengthView.setText("");
            passwordStrengthView.setTextColor(Color.BLACK);
            return;
        }

        String strength;
        int strengthColor;

        if (password.length() >= 10 && password.matches(".*[a-zA-Z]+.*") && password.matches(".*[0-9]+.*") && password.matches(".*[@#$%^&+=].*")) {
            strength = "Strong";
            strengthColor = Color.GREEN;
        } else if (password.length() >= 6) {
            strength = "Medium";
            strengthColor = Color.YELLOW;
        } else {
            strength = "Weak";
            strengthColor = Color.RED;
        }

        passwordStrengthView.setText(strength);
        passwordStrengthView.setTextColor(strengthColor);
    }

}