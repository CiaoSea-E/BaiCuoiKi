package com.example.baicuoiki;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlkhuyenmai.KhuyenMaiMainActivity;
import com.example.qlkhuyenmai.cskh.CSKHActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnSupplier, btnSchedule, btnCustomer, btnPromotion, btnCSKH, btnProductManage;
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

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        // Danh sách trống như yêu cầu, bạn sẽ insert từ app sau
        if (productList != null) {
            productList.clear();
            productAdapter.notifyDataSetChanged();
        }
    }

    private void initViews() {
        btnSupplier    = findViewById(R.id.btnSupplier);
        btnSchedule    = findViewById(R.id.btnSchedule);
        btnCustomer    = findViewById(R.id.btnCustomer);
        btnPromotion   = findViewById(R.id.btnPromotion);
        btnCSKH        = findViewById(R.id.btnCSKH);
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
            btnSupplier.setOnClickListener(v -> startActivity(new Intent(this, SupplierActivity.class)));
        }

        if (btnSchedule != null) {
            btnSchedule.setOnClickListener(v -> startActivity(new Intent(this, LichLamActivity.class)));
        }

        if (btnCustomer != null) {
            btnCustomer.setOnClickListener(v -> startActivity(new Intent(this, CustomerActivity.class)));
        }

        if (btnPromotion != null) {
            btnPromotion.setOnClickListener(v -> startActivity(new Intent(this, KhuyenMaiMainActivity.class)));
        }

        if (btnCSKH != null) {
            btnCSKH.setOnClickListener(v -> startActivity(new Intent(this, CSKHActivity.class)));
        }

        if (btnProductManage != null) {
            btnProductManage.setOnClickListener(v -> startActivity(new Intent(this, ProductActivity.class)));
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
                } else if (id == R.id.nav_order) {
                    startActivity(new Intent(this, OrderActivity.class));
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
