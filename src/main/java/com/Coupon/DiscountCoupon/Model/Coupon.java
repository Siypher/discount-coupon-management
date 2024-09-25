package com.Coupon.DiscountCoupon.Model;



import java.time.LocalDate;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;            // Coupon Id

    @Enumerated(EnumType.STRING)
    private CouponTypes type;   // Coupon Type(enum)

    @Embedded
    private CouponDetails details; // details regarding the coupon

    private LocalDate stopDate;  // The expiration date for the coupon
    private int cLimit;           // How many times the coupon can be applied

}