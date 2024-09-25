package com.Coupon.DiscountCoupon.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long productId;
    private int quantity;
    private double price;
    private double total_discount;

    // Constructor for free items
    public CartItem(Long productId, int quantity, double price, boolean isFree) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.total_discount = isFree ? price * quantity : 0.0;
    }
}
