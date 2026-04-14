package com.example.baicuoiki;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import database.DatabaseHelper;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(CartItem newItem) {
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(newItem.getProductId())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                return;
            }
        }
        cartItems.add(newItem);
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void removeItem(String productId) {
        cartItems.removeIf(item -> item.getProductId().equals(productId));
    }

    public void updateQuantity(String productId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public double getTotalCartPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }

    // Logic đặt hàng (Lưu vào Database)
    public boolean placeOrder(Context context, String customerId, String paymentMethod) {
        if (cartItems.isEmpty()) return false;

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            String orderId = "HD" + System.currentTimeMillis();
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            // 1. Thêm vào bảng HOA_DON
            ContentValues orderValues = new ContentValues();
            orderValues.put("maHoadon", orderId);
            orderValues.put("maKhachHang", customerId);
            orderValues.put("ngayTaohoadon", date);
            orderValues.put("pThucThanhToan", paymentMethod);
            orderValues.put("tongTienTT", getTotalCartPrice());
            orderValues.put("trangThaiDH", "Chờ xác nhận");
            
            long result = db.insert("HOA_DON", null, orderValues);
            if (result == -1) return false;

            // 2. Thêm vào bảng CHI_TIET_HOA_DON
            for (CartItem item : cartItems) {
                ContentValues detailValues = new ContentValues();
                detailValues.put("maHoadon", orderId);
                detailValues.put("maSanpham", item.getProductId());
                detailValues.put("soLuong", item.getQuantity());
                detailValues.put("giaBan", item.getPrice());
                db.insert("CHI_TIET_HOA_DON", null, detailValues);
                
                // Cập nhật số lượng tồn kho (Nếu cần logic này)
                // db.execSQL("UPDATE SAN_PHAM SET soLuongTon = soLuongTon - ? WHERE maSanpham = ?", 
                //            new Object[]{item.getQuantity(), item.getProductId()});
            }

            db.setTransactionSuccessful();
            clearCart();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}