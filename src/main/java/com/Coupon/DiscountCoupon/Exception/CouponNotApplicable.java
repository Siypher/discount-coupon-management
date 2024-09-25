package com.Coupon.DiscountCoupon.Exception;

public class CouponNotApplicable extends RuntimeException {
    public CouponNotApplicable(long id){
        super("Coupon with id="+id+" not applicable for the current products");
    }

}
