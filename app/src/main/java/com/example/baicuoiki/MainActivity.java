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

    private LinearLayout btnSupplier, btnSchedule, btnCustomer;
    private BottomNavigationView bottomNavigation;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initViews();
            setupRecyclerView();
            setupMenuClickEvents();
            setupBottomNavigation();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khởi tạo giao diện!", Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        btnSupplier    = findViewById(R.id.btnSupplier);
        btnSchedule    = findViewById(R.id.btnSchedule);
        btnCustomer    = findViewById(R.id.btnCustomer);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        rvProducts = findViewById(R.id.rvProducts);
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        // Demo data
        productList.add(new Product("P01", "Giày Conver_Chuck Taylor 1970s", 300140, "https://down-vn.img.susercontent.com/file/vn-11134207-7r98o-llt8l6x5z2f39c", "Mô tả giày", 10));
        productList.add(new Product("P02", "Giày Thể Thao C.V Taylor 1970s", 300140, "https://down-vn.img.susercontent.com/file/vn-11134207-7qukw-lkx6n9o1y0u74a", "Mô tả giày", 5));
        productList.add(new Product("P03", "Giày_Adidas Samba đủ màu", 279500, "https://down-vn.img.susercontent.com/file/vn-11134207-7qukw-lgx7v9v7v9v7v9", "Mô tả giày", 8));
        productList.add(new Product("P04", "Giày Thể Thao CV Taylor Black", 300140, "https://down-vn.img.susercontent.com/file/vn-11134207-7qukw-lkx6n9o1y0u74a", "Mô tả giày", 0));

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
