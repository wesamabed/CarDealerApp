package com.example.project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements CarAdapter.CarItemClickListener {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private List<CarType> carList;
    public FavoritesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservations, container, false);
        recyclerView = view.findViewById(R.id.recyclerCarList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = FirebaseFirestore.getInstance();
        fetchUserReservedCarIds();
        return view;
    }

    private void fetchUserReservedCarIds() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = mAuth.getCurrentUser().getEmail();

        if (userId == null) {
            Log.e("fetchUserReservedCarIds", "No authenticated user found.");
            return;
        }




        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> reservedCarIds = (List<String>) documentSnapshot.get("favoriteCarIds");
                        if (reservedCarIds != null && !reservedCarIds.isEmpty()) {
                            fetchCarDataFromFirestore(reservedCarIds);
                        } else {
                            Log.d("fetchUserReservedCarIds", "User has no reserved car IDs.");
                            // Handle the case when there are no reserved car IDs
                        }
                    } else {
                        Log.e("fetchUserReservedCarIds", "User document does not exist.");
                    }
                })
                .addOnFailureListener(e -> Log.e("fetchUserReservedCarIds", "Error fetching user document", e));
    }




    private void fetchCarDataFromFirestore(List<String> reservedCarIds) {
        if (reservedCarIds == null || reservedCarIds.isEmpty()) {
            // Handle the case where there are no reserved cars
            Log.d("CarMenuFragment", "No reserved car IDs available.");
            carList.clear();
            carAdapter.notifyDataSetChanged();
            return;
        }

        db.collection("cars")
                .whereIn("id", reservedCarIds)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        carList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CarType car = document.toObject(CarType.class);
                            Log.d("CarMenuFragment", "Fetched Car: " + car.getName() + ", ID: " + car.getId());
                            carList.add(car);
                        }
                        // Update RecyclerView
                        carAdapter = new CarAdapter(carList, this);
                        recyclerView.setAdapter(carAdapter);
                    } else {
                        Log.w("CarMenuFragment", "Error getting documents.", task.getException());
                    }
                });
    }

    @Override
    public void onCarItemClick(CarType carType) {
        // Open the CarDetailsFragment with the selected car's details
        CarDetailsFragment detailsFragment = CarDetailsFragment.newInstance(carType);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

}
