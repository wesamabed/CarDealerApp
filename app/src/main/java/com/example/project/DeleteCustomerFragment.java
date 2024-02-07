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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DeleteCustomerFragment extends Fragment implements CarAdapterUser.CarItemClickListener {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private CarAdapterUser carAdapterUser;
    private List<CarType> carList;
    private List<User> user;
    public DeleteCustomerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_customer, container, false);
        recyclerView = view.findViewById(R.id.recyclerCarList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = FirebaseFirestore.getInstance();
        fetchUserReservedCarIds();
        return view;
    }

    private void fetchUserReservedCarIds() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null || currentUser.getEmail() == null) {
            Log.e("CarMenuFragment", "No authenticated user found.");
            return;
        }

        String userEmail = currentUser.getEmail();

        db.collection("carDealers").document(userEmail)
                .get()
                .addOnSuccessListener(carDealerSnapshot -> {
                    if (carDealerSnapshot.exists()) {
                        String carDealerId = carDealerSnapshot.getString("carDealerId");
                        if (carDealerId != null) {
                            db.collection("companies").document(carDealerId)
                                    .get()
                                    .addOnSuccessListener(companySnapshot -> {
                                        if (companySnapshot.exists()) {
                                            List<String> carIds = (List<String>) companySnapshot.get("carIds");
                                            if (carIds != null && !carIds.isEmpty()) {
                                                fetchCarsData(db, carIds);
                                            } else {
                                                Log.d("CarMenuFragment", "No car IDs found for the dealer.");
                                            }
                                        } else {
                                            Log.e("CarMenuFragment", "Company document does not exist.");
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("CarMenuFragment", "Error fetching company document", e));
                        } else {
                            Log.e("CarMenuFragment", "Car dealer ID is null.");
                        }
                    } else {
                        Log.e("CarMenuFragment", "Car dealer document does not exist.");
                    }
                })
                .addOnFailureListener(e -> Log.e("CarMenuFragment", "Error fetching car dealer document", e));
    }

    private void fetchCarsData(FirebaseFirestore db, List<String> carIds) {
        carList = new ArrayList<>();
        db.collection("cars")
                .whereIn("id", carIds)
                .whereEqualTo("isReserved", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            CarType car = snapshot.toObject(CarType.class);
                            Log.d("CarMenuFragment", "Fetched Reserved Car: " + car.getName() + ", ID: " + car.getId());
                            carList.add(car);
                        }
                        // Update RecyclerView
                        carAdapterUser = new CarAdapterUser(carList, this);
                        recyclerView.setAdapter(carAdapterUser);
                    } else {
                        Log.d("CarMenuFragment", "No reserved cars found.");
                        carList.clear();
                        if (carAdapterUser != null) {
                            carAdapterUser.notifyDataSetChanged();
                        }

                    }
                })
                .addOnFailureListener(e -> Log.e("CarMenuFragment", "Error fetching reserved cars", e));
    }



    public void onCarItemClick(CarType carType) {
        // Open the CarDetailsFragmentR with the selected car's details
        CarDetailsFragmentR detailsFragment = CarDetailsFragmentR.newInstance(carType);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, detailsFragment)
                .addToBackStack(null)
                .commit();
    }

}
