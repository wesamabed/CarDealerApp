package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.project.CarType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(view -> fetchCarTypes());
    }

    private void fetchCarTypes() {
        String url = "https://658582eb022766bcb8c8c86e.mockapi.io/api/mock/rest-apis/encs5150/car-types";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        ArrayList<CarType> carTypes = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString("id");
                            String type = jsonObject.getString("type");
                            carTypes.add(new CarType(id, type));
                        }
                        // Complete carTypes with data from JSON file
                        ArrayList<CarType> completedCarTypes = completeCarTypesWithData(carTypes);
                        CarDataManager.getInstance().setCarTypes(completedCarTypes);
                      sendCarTypesToFirestore(completedCarTypes);
                        logArrayList(completedCarTypes);
                        navigateToLoginRegistration();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        displayErrorMessage();
                    }
                }, error -> displayErrorMessage());

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private ArrayList<CarType> completeCarTypesWithData(ArrayList<CarType> carTypes) {
        ArrayList<CarType>  completedCarTypes = new ArrayList<>();

        try {
            InputStream inputStream = getResources().openRawResource(R.raw.cars);
            Scanner scanner = new Scanner(inputStream);
            StringBuilder builder = new StringBuilder();

            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }

            JSONArray jsonArray = new JSONArray(builder.toString());

            for (CarType carType : carTypes) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    if (carType.getId().equals(id)) {
                        String name = jsonObject.getString("name");
                        String model = jsonObject.getString("model");
                        double price = jsonObject.getDouble("price");
                        String imageResourceId = jsonObject.getString("imageUrl");
                        String fuelType = jsonObject.getString("fuelType");
                        int kilometers = jsonObject.getInt("kilometers");
                        boolean isReserved = jsonObject.getBoolean("isReserved");
                        String reservationFrom = jsonObject.getString("reservationFrom");
                        String reservationTo = jsonObject.getString("reservationTo");
                        String carDealerId = jsonObject.getString("carDealerId");
                        carType.setName(name);
                        carType.setModel(model);
                        carType.setPrice(price);
                        carType.setImageUrl(imageResourceId);
                        carType.setFuelType(fuelType);
                        carType.setKilometers(kilometers);
                        carType.setReserved(isReserved);
                        carType.setReservationFrom(reservationFrom);
                        carType.setReservationTo(reservationTo);
                        carType.setCarDealerId(carDealerId);
                        completedCarTypes.add(carType);
                    }
                }
            }

            scanner.close();
            inputStream.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return completedCarTypes;
    }

    private void logArrayList(ArrayList<CarType> carTypes) {
        for (CarType carType : carTypes) {
            Log.d("CarType", "ID: " + carType.getId());
            Log.d("CarType", "Type: " + carType.getType());
            Log.d("CarType", "Name: " + carType.getName());
            Log.d("CarType", "Model: " + carType.getModel());
            Log.d("CarType", "Price: " + carType.getPrice());
            Log.d("CarType", "Image URL: " + carType.getImageUrl());
        }
    }


    private void navigateToLoginRegistration() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

        Toast.makeText(MainActivity.this, "Connection Successful", Toast.LENGTH_SHORT).show();
    }

    private void displayErrorMessage() {
        Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }


    private void sendCarTypesToFirestore(ArrayList<CarType> carTypes) {
        for (CarType carType : carTypes) {
            Map<String, Object> carData = new HashMap<>();
            carData.put("id", carType.getId());
            carData.put("type", carType.getType());
            carData.put("name", carType.getName());
            carData.put("model", carType.getModel());
            carData.put("price", carType.getPrice());
            carData.put("imageUrl", carType.getImageUrl());
            carData.put("fuelType", carType.getFuelType());
            carData.put("kilometers", carType.getKilometers());
            carData.put("isReserved", carType.isReserved());
            carData.put("isSpecialOffers", carType.isSpecialOffers());
            carData.put("reservationFrom", carType.getReservationFrom());
            carData.put("reservationTo", carType.getReservationTo());
            carData.put("carDealerId", carType.getCarDealerId());
            carData.put("reservedto", carType.getReservedto());


            // Using carType.getId() as the document ID
            db.collection("cars").document(carType.getId()).set(carData)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));
        }
    }




}
