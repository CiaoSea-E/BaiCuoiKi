package com.example.baicuoiki;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onChange();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.tvName.setText(item.getProductName());
        
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        holder.tvPrice.setText(formatter.format(item.getPrice()) + "đ");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        
        holder.imgProduct.setImageResource(R.drawable.ic_shopping_cart);

        holder.btnPlus.setOnClickListener(v -> {
            item.incrementQuantity();
            notifyItemChanged(position);
            listener.onChange();
        });

        holder.btnMinus.setOnClickListener(v -> {
            item.decrementQuantity();
            notifyItemChanged(position);
            listener.onChange();
        });

        holder.btnRemove.setOnClickListener(v -> {
            CartManager.getInstance().removeItem(item.getProductId());
            notifyDataSetChanged();
            listener.onChange();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, btnRemove;
        TextView tvName, tvPrice, tvQuantity, btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgCartProduct);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            tvName = itemView.findViewById(R.id.tvCartProductName);
            tvPrice = itemView.findViewById(R.id.tvCartProductPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}