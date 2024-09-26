package com.Coupon.DiscountCoupon.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Coupon.DiscountCoupon.Exception.CouponNotFoundException;
import com.Coupon.DiscountCoupon.Model.Cart;
import com.Coupon.DiscountCoupon.Model.Coupon;
import com.Coupon.DiscountCoupon.Model.DiscountForCoupon;
import com.Coupon.DiscountCoupon.Service.CouponService;

@RestController
@RequestMapping("/coupons")
public class CouponController {
    
    @Autowired
    CouponService couponService;

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        Coupon savedCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.status(201).body(savedCoupon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable long id, @RequestBody Coupon coupon){

        Coupon updatedCoupon = couponService.updateCoupon(id,coupon);
        return ResponseEntity.status(201).body(updatedCoupon);
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons(){
        return ResponseEntity.status(200).body(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable long id){
        Coupon coupon = couponService.getCouponById(id).orElseThrow(() -> new CouponNotFoundException(id));
        // return ResponseEntity.status(200).body(coupon);
        return ResponseEntity.ok(coupon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable long id){
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
        
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<Cart> applyCoupon(@PathVariable long couponId, @RequestBody Cart cart){
        
        Coupon coupon = couponService.getCouponById(couponId).orElseThrow(() -> new CouponNotFoundException(couponId));
        Cart updatedCart = couponService.applyCoupon(coupon,cart);
        return ResponseEntity.ok(updatedCart);
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<DiscountForCoupon>> applicableCoupons(Cart cart){
        List<DiscountForCoupon> discountedList = couponService.applicableCoupons(cart);
        return ResponseEntity.ok(discountedList);
    }
}
