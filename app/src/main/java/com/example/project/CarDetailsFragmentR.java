package com.example.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


public class CarDetailsFragmentR extends Fragment {

    private CarType carType;

    public static CarDetailsFragmentR newInstance(CarType carType) {
        CarDetailsFragmentR fragment = new CarDetailsFragmentR();
        Bundle args = new Bundle();
        // Add car details to the arguments
        args.putSerializable("carType", carType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            carType = (CarType) getArguments().getSerializable("carType");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_details_r, container, false);

        // Initialize views and display car details
        TextView nameTextView = view.findViewById(R.id.carDetailNameTextView);
        nameTextView.setText(carType.getName());
        TextView typeTextView = view.findViewById(R.id.carDetailTypeTextView);
        typeTextView.setText(carType.getType());
        TextView modelTextView = view.findViewById(R.id.carDetailModelTextView);
        modelTextView.setText(carType.getModel());
        TextView priceTextView = view.findViewById(R.id.carDetailPriceTextView);
        priceTextView.setText("$"+carType.getPrice());
        ImageView carImageView = view.findViewById(R.id.carDetailImageView);
        Glide.with(carImageView.getContext())
                .load(carType.getImageUrl())
                .placeholder(R.drawable.car_type_2) // Optional placeholder
                .error(R.drawable.car_type_2) // Optional error image
                .into(carImageView);
        TextView fuelTypeTextView = view.findViewById(R.id.carDetailFuelTypeTextView);
        TextView kilometersTextView = view.findViewById(R.id.carDetailKilometersTextView);
        TextView dealerPhoneTextView = view.findViewById(R.id.carDetailDealerPhoneTextView);
        TextView dealerLocationTextView = view.findViewById(R.id.carDetailDealerLocationTextView);
        TextView reservationFromTextView = view.findViewById(R.id.reservationFromTextView);
        TextView reservationToTextView = view.findViewById(R.id.reservationToTextView);
        TextView ReservedTextView = view.findViewById(R.id.ReservedTextView);
        TextView carDealerIdTextView = view.findViewById(R.id.CarDealerNameTextView);

        fuelTypeTextView.setText("Fuel Type: " + carType.getFuelType());
        kilometersTextView.setText("Kilometers: " + carType.getKilometers());


        reservationFromTextView.setText("Reservation From: " + carType.getReservationFrom());
        reservationToTextView.setText("Reservation To: " + carType.getReservationTo());
        ReservedTextView.setText("IS Reserved: " + carType.isReserved());
        carDealerIdTextView.setText("CarDealer Name: " + carType.getCarDealerId());

        dealerPhoneTextView.setText("Phone: " + "0595065849"); // Replace with actual data
        dealerLocationTextView.setText("Location: " + "SAMPLE_LOCATION"); // Replace with actual data
        dealerPhoneTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:SAMPLE_PHONE")); // Replace with actual phone number
            startActivity(intent);
        });

        // Click listener for location
        dealerLocationTextView.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=SAMPLE_LOCATION"); // Replace with actual location
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });


        return view;
    }
    private void addCarToFavorites(String carId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userId = mAuth.getCurrentUser().getEmail(); // Assuming the user ID is their email
        db.collection("users").document(userId)
                .update("favoriteCarIds", FieldValue.arrayUnion(carId))
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Added to favorites!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add to favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }






}
