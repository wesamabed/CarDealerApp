package com.example.project;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                        logArrayList(carTypes);
                        navigateToLoginRegistration();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        displayErrorMessage();
                    }
                }, error -> displayErrorMessage());

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void logArrayList(ArrayList<CarType> carTypes) {
        for (CarType carType : carTypes) {
            Log.d("CarType", carType.toString());
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
}
