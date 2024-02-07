package com.example.project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ReservationDialogFragment extends DialogFragment {

    private CarType carType;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public interface ReservationCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }

    private ReservationCallback callback;

    public static ReservationDialogFragment newInstance(CarType carType) {
        ReservationDialogFragment fragment = new ReservationDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("carType", carType);
        fragment.setArguments(args);
        return fragment;
    }

    public void setReservationCallback(ReservationCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            carType = (CarType) getArguments().getSerializable("carType");
        }
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reserve_car, null);

        ImageView carImageView = dialogView.findViewById(R.id.carDetailImageView);
        TextView carNameTextView = dialogView.findViewById(R.id.carNameTextView);
        TextView carDealerNameTextView = dialogView.findViewById(R.id.carDealerNameTextView);
        TextView carDealerPhoneTextView = dialogView.findViewById(R.id.carDealerPhoneTextView);
        TextView pricePerDayTextView = dialogView.findViewById(R.id.pricePerDayTextView);
        TextView totalPriceTextView = dialogView.findViewById(R.id.totalPriceTextView);
        totalPriceTextView.setText(String.format(Locale.getDefault(), "Total Price: $0.00"));
        setupDialogViewContent(dialogView);
        setupDatePickers(dialogView);

        AlertDialog dialog = builder.setView(dialogView)
                .setPositiveButton("Confirm Reservation", null) // Listener set later
                .setNegativeButton("Cancel", (dialogInterface, id) -> {
                    if (getDialog() != null) {
                        getDialog().cancel();
                    }
                })
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                // Only proceed if dates are selected
                TextView reservationFromTextView = dialog.findViewById(R.id.reservationFromTextView);
                TextView reservationToTextView = dialog.findViewById(R.id.reservationToTextView);
                if (reservationFromTextView.getText().toString().isEmpty() || reservationToTextView.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please select reservation dates.", Toast.LENGTH_SHORT).show();
                    return;
                }
                handleReservation();
            });
        });

        return dialog;
    }

    private void setupDialogViewContent(View view) {
        TextView carNameTextView = view.findViewById(R.id.carNameTextView);
        TextView carDealerNameTextView = view.findViewById(R.id.carDealerNameTextView);
        TextView pricePerDayTextView = view.findViewById(R.id.pricePerDayTextView);
        ImageView carImageView = view.findViewById(R.id.carDetailImageView);
        Glide.with(carImageView.getContext())
                .load(carType.getImageUrl())
                .into(carImageView);
        carNameTextView.setText(carType.getName());
        carDealerNameTextView.setText("Dealer: " + carType.getCarDealerId());
        pricePerDayTextView.setText(String.format(Locale.getDefault(), "Price per Day: $%.2f", carType.getPrice()));
    }

    private void handleReservation() {
        if (!isAdded() || getContext() == null) {
            Log.e("ReservationDialog", "Fragment not added or context is null.");
            return;
        }

        TextView reservationFromTextView = getDialog().findViewById(R.id.reservationFromTextView);
        TextView reservationToTextView = getDialog().findViewById(R.id.reservationToTextView);
        String reservationFrom = reservationFromTextView.getText().toString();
        String reservationTo = reservationToTextView.getText().toString();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String reservedto = currentUser.getEmail();
        if ("Reservation From".equals(reservationFrom) || "Reservation To".equals(reservationTo)) {
            Toast.makeText(getContext(), "Please select reservation dates.", Toast.LENGTH_SHORT).show();
            return;
        }
        double totalPrice = calculateTotalPrice(reservationFrom, reservationTo, carType.getPrice());
        if (totalPrice < 0) {
            Toast.makeText(getContext(), "Invalid reservation dates.", Toast.LENGTH_SHORT).show();
            return;
        }
        TextView totalPriceTextView = getDialog().findViewById(R.id.totalPriceTextView);
        totalPriceTextView.setText(String.format(Locale.getDefault(), "Total Price: $%.2f", totalPrice));
        // Check if the car is already reserved
        db.collection("cars").document(carType.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    CarType latestCarType = documentSnapshot.toObject(CarType.class);
                    if (latestCarType != null && latestCarType.isReserved()) {
                        if (callback != null) {
                            callback.onFailure("This car is already reserved.");
                        }
                        return;
                    }
                    // Update the car as reserved and add it to the user's reserved car list
                    updateCarReservation(latestCarType, reservationFrom, reservationTo, totalPrice,reservedto);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure("Error fetching car data: " + e.getMessage());
                    }
                });
    }

    private void updateCarReservation(CarType car, String from, String to, double totalPrice, String reservedto) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Update the car reservation details
        db.collection("cars").document(car.getId())
                .update("isReserved", true, "reservationFrom", from, "reservationTo", to, "reservedto", reservedto)
                .addOnSuccessListener(aVoid -> {
                    // Call the method to update user's reserved car IDs
                    updateUserReservedCarIds(car.getId(), totalPrice);

                    // Update the 'companies' collection
                    String carDealerId = car.getCarDealerId();
                    if (carDealerId != null && !carDealerId.isEmpty()) {
                        db.collection("companies").document(carDealerId)
                                .update("reservedCarIds", FieldValue.arrayUnion(car.getId()),"userreservedCar", FieldValue.arrayUnion(currentUser.getEmail()))
                                .addOnSuccessListener(aVoidCompany -> Log.d("updateCarReservation", "Company's reservedCarIds updated."))
                                .addOnFailureListener(e -> Log.e("updateCarReservation", "Error updating company document", e));
                    }

                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure("Error updating car status: " + e.getMessage());
                    }
                });
    }


    // Inside ReservationDialogFragment.java

    private void updateUserReservedCarIds(String carId, double totalPrice) {
        String userId = mAuth.getCurrentUser().getEmail();
        db.collection("users").document(userId)
                .update("reservedCarIds", FieldValue.arrayUnion(carId))
                .addOnSuccessListener(aVoid -> {
                    // Check if the fragment is still attached to the context
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Car reserved for $" + totalPrice, Toast.LENGTH_SHORT).show();
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Error updating user's reservation list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        if (callback != null) {
                            callback.onFailure("Error updating user's reservation list: " + e.getMessage());
                        }
                    }
                });
    }


    private double calculateTotalPrice(String from, String to, double pricePerDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        try {
            Date startDate = sdf.parse(from);
            Date endDate = sdf.parse(to);

            // Check if the dates are valid
            if (startDate == null || endDate == null || startDate.after(endDate)) {
                return -1; // Invalid date range
            }

            long diff = endDate.getTime() - startDate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            return days * pricePerDay;
        } catch (ParseException | java.text.ParseException e) {
            Log.e("ReservationDialog", "Error parsing dates: " + e.getMessage());
            return -1; // Indicates an error
        }
    }

    private void setupDatePickers(View view) { TextView reservationFromTextView = view.findViewById(R.id.reservationFromTextView);
        TextView reservationToTextView = view.findViewById(R.id.reservationToTextView);
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener fromDateListener = (datePickerView, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(reservationFromTextView, calendar);
            if (getDialog() != null && getDialog().isShowing()) {
                updateTotalPrice();
            }
        };

        DatePickerDialog.OnDateSetListener toDateListener = (datePickerView, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(reservationToTextView, calendar);
            if (getDialog() != null && getDialog().isShowing()) {
                updateTotalPrice();
            }
        };
        reservationFromTextView.setOnClickListener(v -> new DatePickerDialog(getContext(), fromDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
        reservationToTextView.setOnClickListener(v -> new DatePickerDialog(getContext(), toDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateTotalPrice() {
        if (!isAdded()) {
            return; // Early return if the fragment is not added to the context
        }

        TextView reservationFromTextView = getDialog().findViewById(R.id.reservationFromTextView);
        TextView reservationToTextView = getDialog().findViewById(R.id.reservationToTextView);
        TextView totalPriceTextView = getDialog().findViewById(R.id.totalPriceTextView);

        String from = reservationFromTextView.getText().toString();
        String to = reservationToTextView.getText().toString();
        double totalPrice = calculateTotalPrice(from, to, carType.getPrice());

        if (totalPrice >= 0) {
            totalPriceTextView.setText(String.format(Locale.getDefault(), "Total Price: $%.2f", totalPrice));
        } else {
            totalPriceTextView.setText("Total Price: $0.00");
        }
    }


    private void updateLabel(TextView textView, Calendar calendar) {
        String format = "MM/dd/yyyy"; // Define your date format
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        textView.setText(sdf.format(calendar.getTime()));

    }
}
