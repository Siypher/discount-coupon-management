package com.Coupon.DiscountCoupon.Exception;

public class CouponExpiredException extends RuntimeException {
    public CouponExpiredException() {
        super("Coupon Expired!");
    }
}
