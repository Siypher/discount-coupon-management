package com.Coupon.DiscountCoupon.Exception;

public class CouponNotFoundException extends RuntimeException{
    public CouponNotFoundException(long id){
        super("Coupon with id="+id+" not found");
    }
}
