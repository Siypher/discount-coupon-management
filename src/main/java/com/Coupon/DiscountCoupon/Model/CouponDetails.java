package com.Coupon.DiscountCoupon.Model;


import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponDetails {
    private double threshold;  
    private double discount;
    private long product_id;
    
    @ElementCollection
    private List<BxGyProducts> buy_products;

    @ElementCollection
    private List<BxGyProducts> get_products;

    private int repetition_limit;

}
