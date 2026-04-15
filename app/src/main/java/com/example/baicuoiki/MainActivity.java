package com.example.baicuoiki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnSupplier, btnSchedule, btnCustomer, btnProductManage;
    private BottomNavigationView bottomNavigation;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    // Bỏ ProductDAO vì đã gộp logic vào Activity
    // private ProductDAO productDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // productDAO = new ProductDAO(this);
            initViews();
            setupRecyclerView();
            setupMenuClickEvents();
            setupBottomNavigation();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khởi tạo giao diện!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        // Mock data hoặc lấy từ DB trực tiếp nếu cần, tạm thời để trống để tránh crash do DAO bị xóa
        // List<Product> newData = productDAO.getAllProducts();
        // productList.clear();
        // productList.addAll(newData);
        // productAdapter.notifyDataSetChanged();
    }

    private void initViews() {
        btnSupplier    = findViewById(R.id.btnSupplier);
        btnSchedule    = findViewById(R.id.btnSchedule);
        btnCustomer    = findViewById(R.id.btnCustomer);
        btnProductManage = findViewById(R.id.btnProductManage);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        rvProducts = findViewById(R.id.rvProducts);
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
        rvProducts.setNestedScrollingEnabled(false);
    }

    private void setupMenuClickEvents() {
        if (btnSupplier != null) {
            btnSupplier.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, SupplierActivity.class));
            });
        }

        if (btnSchedule != null) {
            btnSchedule.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, LichLamActivity.class));
            });
        }

        if (btnCustomer != null) {
            btnCustomer.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, CustomerActivity.class));
            });
        }

        if (btnProductManage != null) {
            btnProductManage.setOnClickListener(v -> {
                // SỬA LỖI CRASH: Thay ProductManagementActivity.class thành ProductActivity.class
                startActivity(new Intent(MainActivity.this, ProductActivity.class));
            });
        }
    }

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_cart) {
                    startActivity(new Intent(this, CartActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    Toast.makeText(this, "Tài khoản", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
        }
    }
}
