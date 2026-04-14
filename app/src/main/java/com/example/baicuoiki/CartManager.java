package com.example.baicuoiki;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

    // Chỉ tính tổng tiền cho những sản phẩm được chọn
    public double getTotalCartPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getTotalPrice();
            }
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }

    // Logic đặt hàng nâng cao: Chỉ đặt những sản phẩm được chọn
    public String placeOrder(Context context, String customerId, String paymentMethod) {
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }

        if (selectedItems.isEmpty()) return "Vui lòng chọn ít nhất một sản phẩm để đặt hàng";

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            // 1. Kiểm tra tồn kho
            for (CartItem item : selectedItems) {
                Cursor cursor = db.rawQuery("SELECT soLuongTon, tenSanpham FROM SAN_PHAM WHERE maSanpham = ?", 
                                          new String[]{item.getProductId()});
                if (cursor.moveToFirst()) {
                    int stock = cursor.getInt(0);
                    String name = cursor.getString(1);
                    if (item.getQuantity() > stock) {
                        cursor.close();
                        return "Sản phẩm '" + name + "' không đủ hàng (Tồn: " + stock + ")";
                    }
                }
                cursor.close();
            }

            String orderId = "HD" + System.currentTimeMillis();
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            // 2. Thêm vào bảng HOA_DON
            ContentValues orderValues = new ContentValues();
            orderValues.put("maHoadon", orderId);
            orderValues.put("maKhachHang", customerId);
            orderValues.put("ngayTaohoadon", date);
            orderValues.put("pThucThanhToan", paymentMethod);
            orderValues.put("tongTienTT", getTotalCartPrice());
            orderValues.put("trangThaiDH", "Chờ xác nhận");
            
            long result = db.insert("HOA_DON", null, orderValues);
            if (result == -1) return "Lỗi khi tạo hóa đơn";

            // 3. Thêm CHI_TIET_HOA_DON và Cập nhật Tồn kho
            for (CartItem item : selectedItems) {
                ContentValues detailValues = new ContentValues();
                detailValues.put("maHoadon", orderId);
                detailValues.put("maSanpham", item.getProductId());
                detailValues.put("soLuong", item.getQuantity());
                detailValues.put("giaBan", item.getPrice());
                db.insert("CHI_TIET_HOA_DON", null, detailValues);
                
                // Cập nhật số lượng tồn kho
                db.execSQL("UPDATE SAN_PHAM SET soLuongTon = soLuongTon - ? WHERE maSanpham = ?", 
                           new Object[]{item.getQuantity(), item.getProductId()});
            }

            db.setTransactionSuccessful();
            
            // Xóa những sản phẩm đã đặt khỏi giỏ hàng
            cartItems.removeIf(CartItem::isSelected);

            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi hệ thống: " + e.getMessage();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}