package com.example.baicuoiki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnSupplier, btnSchedule;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ các view
        initViews();

        // Xử lý sự kiện click cho menu chính
        setupMenuClickEvents();

        // Xử lý sự kiện cho Bottom Navigation
        setupBottomNavigation();
    }

    private void initViews() {
        btnSupplier = findViewById(R.id.btnSupplier);
        btnSchedule = findViewById(R.id.btnSchedule);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupMenuClickEvents() {
        btnSupplier.setOnClickListener(v -> {
            // Chuyển sang SupplierActivity
            Intent intent = new Intent(MainActivity.this, SupplierActivity.class);
            startActivity(intent);
        });

        btnSchedule.setOnClickListener(v -> {
            // Hiện tại mới chỉ có SupplierActivity nên ScheduleActivity tạm thời hiện Toast
            Toast.makeText(this, "Tính năng Lịch làm việc đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Trang chủ", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_cart) {
                Toast.makeText(this, "Tính năng Giỏ hàng đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Tính năng Tài khoản đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }
}