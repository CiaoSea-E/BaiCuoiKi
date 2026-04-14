package com.example.baicuoiki;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class CartActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Button btnShopNow;
    private LinearLayout layoutEmptyCart, layoutCartContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initViews();
        checkCartState();
        setupClickEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnShopNow = findViewById(R.id.btnShopNow);
        layoutEmptyCart = findViewById(R.id.layoutEmptyCart);
        layoutCartContent = findViewById(R.id.layoutCartContent);
    }

    private void checkCartState() {
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            layoutEmptyCart.setVisibility(View.VISIBLE);
            layoutCartContent.setVisibility(View.GONE);
        } else {
            layoutEmptyCart.setVisibility(View.GONE);
            layoutCartContent.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickEvents() {
        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nút mua sắm ngay
        btnShopNow.setOnClickListener(v -> finish());
    }
}