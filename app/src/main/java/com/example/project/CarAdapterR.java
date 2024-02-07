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

public class CarAdapterR extends RecyclerView.Adapter<CarAdapterR.CarViewHolderR> {

    private List<CarType> carList;

    public CarAdapterR(List<CarType> carList) {
        this.carList = carList;
    }
    public interface CarItemClickListener {
        void onCarItemClick(CarType carType);
    }
    private CarItemClickListener listener;

    @Override
    public CarViewHolderR onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item_r, parent, false);
        return new CarViewHolderR(view);
    }

    public CarAdapterR(List<CarType> carList, CarItemClickListener listener) {
        this.carList = carList;
        this.listener = listener;
    }

    public void onBindViewHolder(CarViewHolderR holder, int position) {
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

    public static class CarViewHolderR extends RecyclerView.ViewHolder {
        TextView carNameTextView, carTypeTextView, carModelTextView, carPriceTextView;
        ImageView carImageView;

        private final Context context;
        public CarViewHolderR(View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            carNameTextView = itemView.findViewById(R.id.carNameTextView);
            carTypeTextView = itemView.findViewById(R.id.carTypeTextView);
            carModelTextView = itemView.findViewById(R.id.carModelTextView);
            carPriceTextView = itemView.findViewById(R.id.carPriceTextView);
            carImageView = itemView.findViewById(R.id.carImageView);

        }
    }
}
