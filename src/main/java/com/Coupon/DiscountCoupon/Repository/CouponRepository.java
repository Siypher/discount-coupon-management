package com.Coupon.DiscountCoupon.Repository;

import com.Coupon.DiscountCoupon.Model.Coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
}
