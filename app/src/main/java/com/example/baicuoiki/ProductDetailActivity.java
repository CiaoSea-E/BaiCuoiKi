package com.example.baicuoiki;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imgDetail, btnBackDetail;
    private TextView tvDetailPrice, tvDetailName, tvDetailID, tvDetailStock, tvDetailUnit, tvDetailExpiry, tvDetailSupplier, tvDetailDesc;
    private TextView btnAddToCart, btnBuyNow;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        
        // Nhận dữ liệu từ Intent
        Product p = (Product) getIntent().getSerializableExtra("PRODUCT_DATA");
        
        if (p != null) {
            displayProductDetails(p);
            
            // Thiết lập sự kiện tương tác giống ngoài trang chủ
            btnAddToCart.setOnClickListener(v -> {
                CartItem item = new CartItem(p.getId(), p.getName(), p.getPrice(), 1, p.getImage());
                CartManager.getInstance().addToCart(item);
                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            });

            btnBuyNow.setOnClickListener(v -> showBuyBottomSheet(p));
            
        } else {
            Toast.makeText(this, "Không có dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnBackDetail.setOnClickListener(v -> finish());
    }

    private void initViews() {
        btnBackDetail = findViewById(R.id.btnBackDetail);
        imgDetail = findViewById(R.id.imgDetail);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailID = findViewById(R.id.tvDetailID);
        tvDetailStock = findViewById(R.id.tvDetailStock);
        tvDetailUnit = findViewById(R.id.tvDetailUnit);
        tvDetailExpiry = findViewById(R.id.tvDetailExpiry);
        tvDetailSupplier = findViewById(R.id.tvDetailSupplier);
        tvDetailDesc = findViewById(R.id.tvDetailDesc);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
    }

    private void displayProductDetails(Product p) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvDetailPrice.setText("₫" + formatter.format(p.getPrice()));
        tvDetailName.setText(p.getName());
        tvDetailID.setText(p.getId());
        tvDetailStock.setText(String.valueOf(p.getStock()));
        tvDetailUnit.setText(p.getUnit() != null && !p.getUnit().isEmpty() ? p.getUnit() : "---");
        tvDetailExpiry.setText(p.getExpiryDate() != null && !p.getExpiryDate().isEmpty() ? p.getExpiryDate() : "---");
        tvDetailSupplier.setText(p.getSupplierId() != null && !p.getSupplierId().isEmpty() ? p.getSupplierId() : "---");
        tvDetailDesc.setText(p.getDescription());

        Glide.with(this)
                .load(p.getImage())
                .placeholder(R.drawable.ic_shopping_cart)
                .error(R.drawable.ic_shopping_cart)
                .into(imgDetail);
    }

    // Sao chép logic BottomSheet từ ProductAdapter để đồng bộ tính năng
    private void showBuyBottomSheet(Product product) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_buy, null);
        bottomSheetDialog.setContentView(view);

        ImageView imgProductSheet = view.findViewById(R.id.imgProductSheet);
        TextView tvNameSheet = view.findViewById(R.id.tvNameSheet);
        TextView tvPriceSheet = view.findViewById(R.id.tvPriceSheet);
        TextView tvQuantitySheet = view.findViewById(R.id.tvQuantitySheet);
        TextView btnMinus = view.findViewById(R.id.btnMinusSheet);
        TextView btnPlus = view.findViewById(R.id.btnPlusSheet);
        ImageView btnClose = view.findViewById(R.id.btnCloseSheet);
        android.widget.Button btnConfirmBuy = view.findViewById(R.id.btnConfirmBuySheet);

        tvNameSheet.setText(product.getName());
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvPriceSheet.setText(formatter.format(product.getPrice()) + "đ");
        Glide.with(this).load(product.getImage()).into(imgProductSheet);
        
        quantity = 1;
        tvQuantitySheet.setText(String.valueOf(quantity));

        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantitySheet.setText(String.valueOf(quantity));
        });

        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantitySheet.setText(String.valueOf(quantity));
            }
        });

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnConfirmBuy.setOnClickListener(v -> {
            CartItem item = new CartItem(product.getId(), product.getName(), product.getPrice(), quantity, product.getImage());
            CartManager.getInstance().buyNow(item);
            bottomSheetDialog.dismiss();
            startActivity(new Intent(this, CheckoutActivity.class));
        });

        bottomSheetDialog.show();
    }
}
