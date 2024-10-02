package com.thufail.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    Button login_btn, register_btn;
    private BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment = new HomeFragment();
    private KeranjangFragment keranjangFragment = new KeranjangFragment();
    private HistoryFragment historyFragment = new HistoryFragment();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        login_btn = (Button) findViewById(R.id.login_btn);
        register_btn = (Button) findViewById(R.id.signupbtn);
        if (login_btn != null) {
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent BukaLgn = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(BukaLgn);
                }
            });
        } else {

        }
        if (register_btn != null) {
            register_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent BukaRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(BukaRegister);
                }
            });
        }

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.Home) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, homeFragment)
                    .commit();
            return true;
        } else if (itemId == R.id.Keranjang) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, keranjangFragment)
                    .commit();
            return true;
        } else if (itemId == R.id.history) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, historyFragment)
                    .commit();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
