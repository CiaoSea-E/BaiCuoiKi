package com.example.baicuoiki;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private int quantity = 1;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        holder.tvPrice.setText(formatter.format(product.getPrice()) + "đ");

        Glide.with(context)
                .load(product.getImage())
                .placeholder(R.drawable.ic_shopping_cart)
                .error(R.drawable.ic_shopping_cart)
                .into(holder.imgProduct);

        // Mở Bottom Sheet khi nhấn Mua ngay
        holder.btnBuyNow.setOnClickListener(v -> showBuyBottomSheet(product));

        holder.btnAddToCart.setOnClickListener(v -> {
            CartItem item = new CartItem(product.getId(), product.getName(), product.getPrice(), 1, product.getImage());
            CartManager.getInstance().addToCart(item);
            Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    private void showBuyBottomSheet(Product product) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_buy, null);
        bottomSheetDialog.setContentView(view);

        ImageView imgProductSheet = view.findViewById(R.id.imgProductSheet);
        TextView tvNameSheet = view.findViewById(R.id.tvNameSheet);
        TextView tvPriceSheet = view.findViewById(R.id.tvPriceSheet);
        TextView tvQuantitySheet = view.findViewById(R.id.tvQuantitySheet);
        TextView btnMinus = view.findViewById(R.id.btnMinusSheet);
        TextView btnPlus = view.findViewById(R.id.btnPlusSheet);
        ImageView btnClose = view.findViewById(R.id.btnCloseSheet);
        Button btnConfirmBuy = view.findViewById(R.id.btnConfirmBuySheet);

        // Load data
        tvNameSheet.setText(product.getName());
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        tvPriceSheet.setText(formatter.format(product.getPrice()) + "đ");
        Glide.with(context).load(product.getImage()).into(imgProductSheet);
        
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
            // Chỉ chọn duy nhất sản phẩm này để thanh toán ngay
            for (CartItem ci : CartManager.getInstance().getCartItems()) {
                ci.setSelected(false);
            }

            CartItem item = new CartItem(product.getId(), product.getName(), product.getPrice(), quantity, product.getImage());
            item.setSelected(true);
            CartManager.getInstance().addToCart(item);

            bottomSheetDialog.dismiss();
            context.startActivity(new Intent(context, CheckoutActivity.class));
        });

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvPrice;
        Button btnBuyNow, btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            btnBuyNow = itemView.findViewById(R.id.btnBuyNow);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
