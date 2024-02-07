package com.example.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ContactFragment extends Fragment {

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        // Find the buttons by their IDs
        Button buttonCall = view.findViewById(R.id.buttonCall);
        Button buttonOpenMaps = view.findViewById(R.id.buttonOpenMaps);
        Button buttonSendEmail = view.findViewById(R.id.buttonSendEmail);

        // Set click listeners for the buttons
        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initiate a phone call
                String phoneNumber = "tel:0599000000";
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(phoneNumber));
                startActivity(intent);
            }
        });

        buttonOpenMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Google Maps to the car dealer's location
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=Car+Dealer");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Gmail with the recipient set to CarDealer@cars.com
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:CarDealer@cars.com"));
                startActivity(emailIntent);
            }
        });

        return view;
    }
}
