package com.example.baicuoiki;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qlkhuyenmai.KhuyenMaiMainActivity;
import com.example.qlkhuyenmai.cskh.CSKHActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnSupplier, btnSchedule, btnCustomer, btnPromotion, btnCSKH, btnProductManage;
    private BottomNavigationView bottomNavigation;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private EditText edtSearchHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Giải quyết vấn đề đổi màu khi cắm sạc (Dark Mode)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initViews();
            setupRecyclerView();
            setupSearch();
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
        loadData(""); // Load toàn bộ khi vào lại
    }

    private void loadData(String keyword) {
        if (productList != null) {
            productList.clear();
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            
            Cursor cursor;
            if (keyword.isEmpty()) {
                cursor = db.rawQuery("SELECT * FROM SAN_PHAM", null);
            } else {
                // Tìm kiếm gần đúng (LIKE) theo tên sản phẩm
                cursor = db.rawQuery("SELECT * FROM SAN_PHAM WHERE tenSanpham LIKE ?", new String[]{"%" + keyword + "%"});
            }
            
            if (cursor.moveToFirst()) {
                do {
                    Product p = new Product();
                    p.setId(cursor.getString(0));
                    p.setName(cursor.getString(1));
                    p.setImage(cursor.getString(2));
                    p.setDescription(cursor.getString(3));
                    p.setUnit(cursor.getString(4));
                    p.setPrice(cursor.getDouble(5));
                    p.setStock(cursor.getInt(6));
                    p.setExpiryDate(cursor.getString(7));
                    p.setStatus(cursor.getString(8));
                    p.setSupplierId(cursor.getString(9));
                    
                    productList.add(p);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            
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
        edtSearchHome = findViewById(R.id.edtSearchHome);
    }

    private void setupSearch() {
        if (edtSearchHome != null) {
            edtSearchHome.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Tìm kiếm ngay khi người dùng gõ phím
                    loadData(s.toString().trim());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
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
