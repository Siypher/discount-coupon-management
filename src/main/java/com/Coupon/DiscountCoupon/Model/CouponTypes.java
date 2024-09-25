package com.Coupon.DiscountCoupon.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CouponTypes {
    CART_WISE("cart-wise"),
    PRODUCT_WISE("product-wise"),
    BXGY("bxgy");

    private final String type;

    CouponTypes(String type) {
        this.type = type;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    // Custom parsing for case-insensitive and hyphenated input
    @JsonCreator
    public static CouponTypes fromString(String type) {
        for (CouponTypes couponType : CouponTypes.values()) {
            if (couponType.getType().equalsIgnoreCase(type)) {
                return couponType;
            }
        }
        throw new IllegalArgumentException("Invalid coupon type: " + type);
    }
}
