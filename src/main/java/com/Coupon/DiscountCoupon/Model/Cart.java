package com.Coupon.DiscountCoupon.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private List<CartItem> items;
    private double totalPrice;
    private double totalDiscount;
    private double finalPrice;

    public double calculateTotalPrice() {
        return items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public CartItem findItemByProductId(Long productId) {
        return items.stream().filter(item -> item.getProductId().equals(productId)).findFirst().orElse(null);
    }

    public void addItem(CartItem item){
        items.add(item);
    }
}