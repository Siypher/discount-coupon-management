package com.Coupon.DiscountCoupon.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountForCoupon {
    private Long couponId;
    private String type;
    private double discount;
}
