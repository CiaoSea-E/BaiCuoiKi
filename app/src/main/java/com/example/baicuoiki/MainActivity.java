package com.example.baicuoiki;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.dangnhap.activities.NhanVienActivity;
import com.example.kho_ketoan.activities.KhoKeToanMainActivity;
import com.example.qlkhuyenmai.KhuyenMaiMainActivity;
import com.example.qlkhuyenmai.cskh.CSKHActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private LinearLayout btnSupplier, btnSchedule, btnCustomer, btnPromotion, btnProductManage, btnKhoKeToan, btnNhanVien;
    private ImageView btnCSKH;
    private LinearLayout layoutAdminMenuContainer;
    private BottomNavigationView bottomNavigation;
    private GridView gvProducts; 
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private EditText edtSearchHome;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initViews();
            applyPermission(); 
            setupGridView();
            setupSearch();
            setupMenuClickEvents();
            setupBottomNavigation();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khởi tạo giao diện!", Toast.LENGTH_LONG).show();
        }
    }

    private void applyPermission() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = prefs.getString("ROLE", "customer");
        String username = prefs.getString("USERNAME", "");

        if (tvWelcome != null) {
            tvWelcome.setText(username.isEmpty() ? "Xin chào!" : "Xin chào, " + username + "!");
        }

        if (role.equalsIgnoreCase("customer")) {
            // Khách hàng: Ẩn toàn bộ menu admin
            if (layoutAdminMenuContainer != null) layoutAdminMenuContainer.setVisibility(View.GONE);
            
            // Hiện icon Tài khoản ở Bottom Nav
            if (bottomNavigation != null) {
                bottomNavigation.getMenu().findItem(R.id.nav_user_info).setVisible(true);
            }
        } else {
            // Admin: Hiện menu admin
            if (layoutAdminMenuContainer != null) layoutAdminMenuContainer.setVisibility(View.VISIBLE);
            
            // Ẩn icon Tài khoản ở Bottom Nav cho Admin vì đã có menu quản lý
            if (bottomNavigation != null) {
                bottomNavigation.getMenu().findItem(R.id.nav_user_info).setVisible(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData("");
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
                cursor = db.rawQuery("SELECT * FROM SAN_PHAM WHERE tenSanpham LIKE ?", new String[]{"%" + keyword + "%"});
            }

            if (cursor != null && cursor.moveToFirst()) {
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
                cursor.close();
            }
            db.close();
            if (productAdapter != null) productAdapter.notifyDataSetChanged();
        }
    }

    private void initViews() {
        layoutAdminMenuContainer = findViewById(R.id.layoutAdminMenuContainer);
        btnSupplier    = findViewById(R.id.btnSupplier);
        btnSchedule    = findViewById(R.id.btnSchedule);
        btnCustomer    = findViewById(R.id.btnCustomer);
        btnPromotion   = findViewById(R.id.btnPromotion);
        btnCSKH        = findViewById(R.id.btnCSKH_Header);
        btnProductManage = findViewById(R.id.btnProductManage);
        btnKhoKeToan   = findViewById(R.id.btnKhoKeToan);
        btnNhanVien    = findViewById(R.id.btnNhanVien);
        tvWelcome      = findViewById(R.id.tvWelcome);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        gvProducts = findViewById(R.id.gvProducts);
        edtSearchHome = findViewById(R.id.edtSearchHome);
    }

    private void setupGridView() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        gvProducts.setAdapter(productAdapter);
    }

    private void setupSearch() {
        if (edtSearchHome != null) {
            edtSearchHome.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    loadData(s.toString().trim());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupMenuClickEvents() {
        if (btnSupplier != null) btnSupplier.setOnClickListener(v -> startActivity(new Intent(this, SupplierActivity.class)));
        if (btnSchedule != null) btnSchedule.setOnClickListener(v -> startActivity(new Intent(this, LichLamActivity.class)));
        if (btnCustomer != null) btnCustomer.setOnClickListener(v -> startActivity(new Intent(this, CustomerActivity.class)));
        if (btnPromotion != null) btnPromotion.setOnClickListener(v -> startActivity(new Intent(this, KhuyenMaiMainActivity.class)));
        if (btnCSKH != null) btnCSKH.setOnClickListener(v -> startActivity(new Intent(this, CSKHActivity.class)));
        if (btnProductManage != null) btnProductManage.setOnClickListener(v -> startActivity(new Intent(this, ProductActivity.class)));
        if (btnKhoKeToan != null) btnKhoKeToan.setOnClickListener(v -> startActivity(new Intent(this, KhoKeToanMainActivity.class)));
        if (btnNhanVien != null) btnNhanVien.setOnClickListener(v -> startActivity(new Intent(this, NhanVienActivity.class)));
    }

    private void setupBottomNavigation() {
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) return true;
                if (id == R.id.nav_cart) {
                    startActivity(new Intent(this, CartActivity.class));
                    return true;
                }
                if (id == R.id.nav_order) {
                    startActivity(new Intent(this, OrderActivity.class));
                    return true;
                }
                if (id == R.id.nav_user_info) {
                    startActivity(new Intent(this, CustomerActivity.class));
                    return true;
                }
                return false;
            });
        }
    }
}
