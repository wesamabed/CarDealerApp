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

public class CarAdapterUser extends RecyclerView.Adapter<CarAdapterUser.CarViewHolderUser> {

    private List<CarType> carList;

    public CarAdapterUser(List<CarType> carList) {
        this.carList = carList;
    }
    public interface CarItemClickListener {
        void onCarItemClick(CarType carType);
    }
    private CarItemClickListener listener;

    @Override
    public CarViewHolderUser onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_reserved_item, parent, false);
        return new CarViewHolderUser(view);
    }

    public CarAdapterUser(List<CarType> carList, CarItemClickListener listener) {
        this.carList = carList;
        this.listener = listener;
    }

    public void onBindViewHolder(CarViewHolderUser holder, int position) {
        CarType car = carList.get(position);
        holder.carNameTextView.setText(car.getName());
        holder.carTypeTextView.setText(car.getType());
        holder.carModelTextView.setText(car.getModel());
        holder.from.setText(car.getReservationFrom());
        holder.to.setText(car.getReservationTo());
        holder.userwhoreserved.setText(car.getReservedto());

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(car.getImageUrl())
                .into(holder.carImageView);

        holder.itemView.setOnClickListener(v -> listener.onCarItemClick(car));

        holder.Delete_Customer.setOnClickListener(v -> {
            String carId = car.getId(); // Assuming CarType has a method to get the car's ID
            String reservedTo = car.getReservedto(); // Assuming CarType has a method to get the reservedTo field

            // Firestore update operation
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("cars").document(carId)
                    .update("isReserved", false,
                            "reservationFrom", null,
                            "reservationTo", null,
                            "reservedto", null)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("CarAdapterUser", "Car reservation details successfully removed.");
                        // Remove the item from the list and notify the adapter
                        carList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, carList.size());

                        // Update user's reservedCarIds array
                        if (reservedTo != null && !reservedTo.isEmpty()) {
                            db.collection("users").document(reservedTo)
                                    .update("reservedCarIds", FieldValue.arrayRemove(carId))
                                    .addOnSuccessListener(aVoidUser -> Log.d("CarAdapterUser", "User reservedCarIds array updated."))
                                    .addOnFailureListener(e -> Log.e("CarAdapterUser", "Error updating user document", e));
                        }
                    })
                    .addOnFailureListener(e -> Log.e("CarAdapterUser", "Error updating document", e));
        });

    }


    @Override
    public int getItemCount() {
        return carList.size();
    }

    public static class CarViewHolderUser extends RecyclerView.ViewHolder {
        TextView carNameTextView, carTypeTextView, carModelTextView, from,to,userwhoreserved;
        ImageView carImageView;
        Button Delete_Customer;

        private final Context context;
        public CarViewHolderUser(View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            carNameTextView = itemView.findViewById(R.id.carNameTextView);
            carTypeTextView = itemView.findViewById(R.id.carTypeTextView);
            carModelTextView = itemView.findViewById(R.id.carModelTextView);
            userwhoreserved = itemView.findViewById(R.id.userwhoreserved);
            to = itemView.findViewById(R.id.to);
            from = itemView.findViewById(R.id.from);
            Delete_Customer = itemView.findViewById(R.id.Delete_Customer);
            carImageView = itemView.findViewById(R.id.carImageView);


        }
    }
}
