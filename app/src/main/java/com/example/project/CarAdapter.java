package com.example.project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<CarType> carList;

    public CarAdapter(List<CarType> carList) {
        this.carList = carList;
    }
    public interface CarItemClickListener {
        void onCarItemClick(CarType carType);
    }
    private CarItemClickListener listener;

    @Override
    public CarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item, parent, false);
        return new CarViewHolder(view);
    }

    public CarAdapter(List<CarType> carList, CarItemClickListener listener) {
        this.carList = carList;
        this.listener = listener;
    }

    public void onBindViewHolder(CarViewHolder holder, int position) {
        CarType car = carList.get(position);
        holder.carNameTextView.setText(car.getName());
        holder.carTypeTextView.setText(car.getType());
        holder.carModelTextView.setText(car.getModel());
        holder.carPriceTextView.setText("$" + car.getPrice());

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(car.getImageUrl())
                .placeholder(R.drawable.car_type_2) // Optional placeholder
                .error(R.drawable.car_type_2) // Optional error image
                .into(holder.carImageView);
        holder.itemView.setOnClickListener(v -> listener.onCarItemClick(car));
        // Set onClickListeners for holder.addToFavoritesButton and holder.reserveButton

        holder.reserveButton.setOnClickListener(v -> {
            if (car.getId() != null) {
                if (holder.context instanceof AppCompatActivity) {
                    ReservationDialogFragment reservationDialog = ReservationDialogFragment.newInstance(car);
                    reservationDialog.setReservationCallback(new ReservationDialogFragment.ReservationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(holder.context, "Reservation successful!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(holder.context, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                    reservationDialog.show(((AppCompatActivity) holder.context).getSupportFragmentManager(), "reservationDialog");
                } else {
                    Toast.makeText(holder.context, "Error: Unable to open reservation dialog.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(holder.context, "The app does not recognize the car", Toast.LENGTH_SHORT).show();
            }
        });
        holder.addToFavoritesButton.setOnClickListener(v -> {
            if (car.getId() != null) {
                addCarToFavorites(car.getId(), holder.context);
            } else {
                Toast.makeText(holder.context, "Unable to add car to favorites", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void addCarToFavorites(String carId, Context context) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userId = mAuth.getCurrentUser().getEmail();
        db.collection("users").document(userId)
                .update("favoriteCarIds", FieldValue.arrayUnion(carId))
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to add to favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }



                    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        TextView carNameTextView, carTypeTextView, carModelTextView, carPriceTextView;
        ImageView carImageView;
        Button addToFavoritesButton, reserveButton;
        private final Context context;
        public CarViewHolder(View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            carNameTextView = itemView.findViewById(R.id.carNameTextView);
            carTypeTextView = itemView.findViewById(R.id.carTypeTextView);
            carModelTextView = itemView.findViewById(R.id.carModelTextView);
            carPriceTextView = itemView.findViewById(R.id.carPriceTextView);
            carImageView = itemView.findViewById(R.id.carImageView);
            addToFavoritesButton = itemView.findViewById(R.id.addToFavoritesButton);
            reserveButton = itemView.findViewById(R.id.reserveButton);
        }
    }
}
