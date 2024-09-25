package com.Coupon.DiscountCoupon.Exception;

public class CouponLimitExceededException extends RuntimeException {
    public CouponLimitExceededException(long id) {
        super("Limit exceeded for coupon with id="+id);
    }
}