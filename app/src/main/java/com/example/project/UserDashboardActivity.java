package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.ActionBarDrawerToggle;

public class UserDashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_user_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Initialize with the Home Fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), "Home");
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Replace the switch-case with if-else statements
            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment(), "Home");
            } else if (itemId == R.id.nav_car_menu) {
                loadFragment(new CarMenuFragment(), "Car Menu");
            } else if (itemId == R.id.nav_reservations) {
                loadFragment(new ReservationsFragment(), "Your Reservations");
            } else if (itemId == R.id.nav_favorites) {
                loadFragment(new FavoritesFragment(), "Your Favorites");
            } else if (itemId == R.id.nav_special_offers) {
                loadFragment(new SpecialOffersFragment(), "Special Offers");
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new ProfileFragment(), "Profile");
            } else if (itemId == R.id.nav_contact) {
                loadFragment(new ContactFragment(), "Contact");
            } else if (itemId == R.id.nav_logout) {
                logoutUser();
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment)
                .commit();
        setTitle(title);
    }

    private void contactCarDealer() {
        // Implement logic to call or find the car dealer
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
