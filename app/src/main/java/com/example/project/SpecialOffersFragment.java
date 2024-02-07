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
import java.util.Collections;
import java.util.List;

public class SpecialOffersFragment extends Fragment implements CarAdapter.CarItemClickListener {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private List<CarType> carList;
    public SpecialOffersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservations, container, false);
        recyclerView = view.findViewById(R.id.recyclerCarList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = FirebaseFirestore.getInstance();
        fetchCarDataFromFirestore();
        return view;
    }




    private void fetchCarDataFromFirestore() {
        db.collection("cars")
                .whereIn("isSpecialOffers", Collections.singletonList(true))
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
