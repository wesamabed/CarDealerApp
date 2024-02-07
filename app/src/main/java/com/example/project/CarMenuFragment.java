package com.example.project;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CarMenuFragment extends Fragment implements CarAdapter.CarItemClickListener {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private List<CarType> carList;
    private Spinner spinnerFuelType;
    private String filterPrice = "";
    private String filterModel = "";
    private String filterName = "";
    private String filterKilometers = "";
    private String filterFuelType = "ALL"; // Assuming 'ALL' as default
    private boolean filterReserved = false;
    public CarMenuFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Indicate that this fragment would like to influence the set of actions in the action bar.
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_car_menu_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            // Inflate the popup layout
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.layout_filter_popup, null);

            // Create the PopupWindow
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    true
            );
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.WHITE));

            // Initialize your UI elements here
            // Example: TextView priceTextView = popupView.findViewById(R.id.PriceTextView);
            EditText priceTextView = popupView.findViewById(R.id.PriceTextView);
            EditText ModelTextView = popupView.findViewById(R.id.ModelTextView);
            EditText NameTextView = popupView.findViewById(R.id.NameTextView);
            EditText KilometersTextView = popupView.findViewById(R.id.KilometersTextView);
            spinnerFuelType = popupView.findViewById(R.id.spinnerFuelType);
            CheckBox checkboxReserved = popupView.findViewById(R.id.checkboxReserved);
            populatespinnerFuelType();

            // Example for priceTextView
            priceTextView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus && priceTextView.getText().toString().equals("Price")) {
                    priceTextView.setText("");
                }
            });

// Do the same for ModelTextView, NameTextView, KilometersTextView
            ModelTextView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus && ModelTextView.getText().toString().equals("Model")) {
                    ModelTextView.setText("");
                }
            });

            NameTextView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus && NameTextView.getText().toString().equals("Name")) {
                    NameTextView.setText("");
                }
            });

            KilometersTextView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus && KilometersTextView.getText().toString().equals("Kilometers")) {
                    KilometersTextView.setText("");
                }
            });











            // Setup listeners for your filter inputs
            Button applyFiltersButton = popupView.findViewById(R.id.buttonApplyFilters);
            applyFiltersButton.setOnClickListener(v -> {
                // Fetch filter inputs
                String price = priceTextView.getText().toString();
                String model = ModelTextView.getText().toString();
                String name = NameTextView.getText().toString();
                String kilometers = KilometersTextView.getText().toString();
                String fuelType = spinnerFuelType.getSelectedItem().toString();
                boolean isReserved = checkboxReserved.isChecked();

                // Apply filters
                applyFilters(price, model, name, kilometers, fuelType, isReserved);

                // Dismiss popup window
                popupWindow.dismiss();
            });

            // Show the popup window
            View anchorView = getActivity().findViewById(R.id.action_search); // Get the search icon view
            popupWindow.showAsDropDown(anchorView);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void applyFilters(String price, String model, String name, String kilometers, String fuelType, boolean isReserved) {
        filterPrice = price.equals("Price") ? "" : price.trim();
        filterModel = model.equals("Model") ? "" : model.trim();
        filterName = name.equals("Name") ? "" : name.trim();
        filterKilometers = kilometers.equals("Kilometers") ? "" : kilometers.trim();
        filterFuelType = fuelType;
        filterReserved = isReserved;

        Log.d("CarMenuFragment", "Applying filters: Price = " + filterPrice + ", Model = " + filterModel + ", Name = " + filterName + ", Kilometers = " + filterKilometers + ", Fuel Type = " + filterFuelType + ", Reserved = " + filterReserved);

        fetchCarDataFromFirestore();
    }



    private void populatespinnerFuelType() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.spinnerFuelType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuelType.setAdapter(adapter);
    }









    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_menu, container, false);

        recyclerView = view.findViewById(R.id.recyclerCarList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        db = FirebaseFirestore.getInstance();
        fetchCarDataFromFirestore();
        return view;
    }
    private void fetchCarDataFromFirestore() {
        db.collection("cars").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<CarType> allCars = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    CarType car = document.toObject(CarType.class);
                    allCars.add(car);
                }
                // Apply filters to allCars and update carList
                carList = applyFiltersToList(allCars);

                // Logging the filtered carList
                logFilteredCarList(carList);

                // Update RecyclerView
                carAdapter = new CarAdapter(carList, this);
                recyclerView.setAdapter(carAdapter);
            } else {
                Log.w("CarMenuFragment", "Error getting documents.", task.getException());
            }
        });
    }

    private void logFilteredCarList(List<CarType> filteredCarList) {
        StringBuilder logMessage = new StringBuilder("Filtered Car List: \n");
        for (CarType car : filteredCarList) {
            logMessage.append("Name: ").append(car.getName())
                    .append(", Model: ").append(car.getModel())
                    .append(", Price: ").append(car.getPrice())
                    .append(", Kilometers: ").append(car.getKilometers())
                    .append(", Fuel Type: ").append(car.getFuelType())
                    .append(", Reserved: ").append(car.isReserved())
                    .append("\n");
        }
        Log.d("CarMenuFragment", logMessage.toString());
    }


    private List<CarType> applyFiltersToList(List<CarType> allCars) {
        return allCars.stream().filter(car -> {
            boolean matches = true;

            // Apply filters only if they are not empty
            if (!filterPrice.isEmpty()) {
                double priceLimit = Double.parseDouble(filterPrice);
                if (car.getPrice() > priceLimit) matches = false;
            }
            if (!filterModel.isEmpty() && !car.getModel().equalsIgnoreCase(filterModel)) {
                matches = false;
            }
            if (!filterName.isEmpty() && !car.getName().equalsIgnoreCase(filterName)) {
                matches = false;
            }
            if (!filterKilometers.isEmpty()) {
                double kilometerLimit = Double.parseDouble(filterKilometers);
                if (car.getKilometers() > kilometerLimit) matches = false;
            }
            if (!filterFuelType.equals("ALL") && !car.getFuelType().equalsIgnoreCase(filterFuelType)) {
                matches = false;
            }
            if (filterReserved && car.isReserved()) {
                matches = false;
            }

            return matches;
        }).collect(Collectors.toList());
    }

    // Utility method to check if a string is numeric
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            Log.e("FilterCheck", "Invalid numeric format for string: " + str, e);
            return false;
        }
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
