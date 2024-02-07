package com.example.project;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragmentCarDealer extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_cardealer, container, false);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadCarDealerData(currentUser.getEmail(), view);
        }
        return view;
    }

    private void loadCarDealerData(String email, View view) {
        db.collection("carDealers").document(email).get()
                .addOnSuccessListener(documentSnapshot -> {
                    CarDealer carDealer = documentSnapshot.toObject(CarDealer.class);
                    if (carDealer != null) {
                        updateUIWithCarDealerData(carDealer, view);
                    }
                })
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error loading car dealer data", e));
    }



    private void updateUIWithCarDealerData(CarDealer carDealer, View view) {

        ImageView carImageView = view.findViewById(R.id.carImageView);
        TextView tvCarDealerId = view.findViewById(R.id.tvCarDealerId);
        TextView tvFirstName = view.findViewById(R.id.tvFirstName);
        TextView tvLastName = view.findViewById(R.id.tvLastName);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        TextView tvGender = view.findViewById(R.id.tvGender);
        TextView tvCountry = view.findViewById(R.id.tvCountry);
        TextView tvCity = view.findViewById(R.id.tvCity);
        TextView tvLocation = view.findViewById(R.id.tvLocation);
        TextView tvCarIdsCount = view.findViewById(R.id.tvCarIdsCount);
        TextView tvReservedCarIdsCount = view.findViewById(R.id.tvReservedCarIdsCount);

        if (carDealer.getImageUrl() != null && !carDealer.getImageUrl().isEmpty()) {
            Glide.with(this).load(carDealer.getImageUrl()).into(carImageView);
        }

        tvCarDealerId.setText("Car Dealer ID: " + carDealer.getCarDealerId());
        tvFirstName.setText("First Name: " + carDealer.getFirstName());
        tvLastName.setText("Last Name: " + carDealer.getLastName());
        tvEmail.setText("Email: " + carDealer.getEmail());
        tvPhoneNumber.setText("Phone Number: " + carDealer.getPhone_number());
        tvGender.setText("Gender: " + carDealer.getGender());
        tvCountry.setText("Country: " + carDealer.getCountry());
        tvCity.setText("City: " + carDealer.getCity());
        tvLocation.setText("Location: " + carDealer.getLocation());

        db.collection("companies").document(carDealer.getCarDealerId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Company company = documentSnapshot.toObject(Company.class);
                    if (company != null) {
                        tvCarIdsCount.setText("Number of Cars: "+company.getCarIds().size());
                        tvReservedCarIdsCount.setText("Number of reserved Cars: "+company.getReservedCarIds().size());
                    }
                })
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error loading car dealer data", e));



    }
}
