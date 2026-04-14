package com.example.baicuoiki;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productId;
    private String productName;
    private double price;
    private int quantity;
    private String image;

    public CartItem(String productId, String productName, double price, int quantity, String image) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = Math.max(1, quantity);
        this.image = image;
    }

    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    
    public void setQuantity(int quantity) {
        if (quantity >= 1) {
            this.quantity = quantity;
        }
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
        }
    }

    public String getImage() { return image; }

    public double getTotalPrice() {
        return price * quantity;
    }
}