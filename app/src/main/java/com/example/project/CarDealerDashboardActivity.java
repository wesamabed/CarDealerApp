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
public class CarDealerDashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_car_dealer_dashboard);
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
            loadFragment(new ProfileFragmentCarDealer(), "Home");
        }
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Replace the switch-case with if-else statements
            if (itemId == R.id.nav_home) {
                loadFragment(new ProfileFragmentCarDealer(), "Home");
            } else if (itemId == R.id.nav_edit_profile) {
                loadFragment(new EditProfileFragment(), "Edit Profile");
            } else if (itemId == R.id.Edit_Car) {
                loadFragment(new EditCarFragment(), "Edit Car");
            } else if (itemId == R.id.nav_Add_Admin) {
                loadFragment(new AddAdminFragment(), "Add Admin");
            } else if (itemId == R.id.nav_Add_Car) {
                loadFragment(new AddCarFragment(), "Add Car");
            } else if (itemId == R.id.View_All_Reserves) {
                loadFragment(new ViewAllReservesFragment(), "View All Reserves");
            } else if (itemId == R.id.Delete_Customer) {
                loadFragment(new DeleteCustomerFragment(), "Delete Customer");
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
