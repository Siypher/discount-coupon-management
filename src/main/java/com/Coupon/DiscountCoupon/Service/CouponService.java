package com.Coupon.DiscountCoupon.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Coupon.DiscountCoupon.Exception.CouponExpiredException;
import com.Coupon.DiscountCoupon.Exception.CouponLimitExceededException;
import com.Coupon.DiscountCoupon.Exception.CouponNotFoundException;
import com.Coupon.DiscountCoupon.Model.BxGyProducts;
import com.Coupon.DiscountCoupon.Model.Cart;
import com.Coupon.DiscountCoupon.Model.CartItem;
import com.Coupon.DiscountCoupon.Model.Coupon;
import com.Coupon.DiscountCoupon.Model.CouponDetails;
import com.Coupon.DiscountCoupon.Model.DiscountForCoupon;
import com.Coupon.DiscountCoupon.Repository.CouponRepository;

@Service
public class CouponService {
    
    @Autowired
    CouponRepository couponRepository;

    public Coupon createCoupon(Coupon coupon){
        if(coupon.getStopDate() != null && coupon.getStopDate().isBefore(LocalDate.now())){
            throw new RuntimeException("Coupon stop date can not be before current date");
        }
        if(coupon.getDetails().getDiscount() <= 0d){
            throw new RuntimeException("Coupon value can not be less than or equal to zero.");
        }
        return couponRepository.save(coupon);
    }

    public Optional<Coupon> getCouponById(long id){
        return couponRepository.findById(id);
    }

    public List<Coupon> getAllCoupons(){
        return couponRepository.findAll();
    }

    public void deleteCoupon(long id){
        if(getCouponById(id).isPresent()){
            couponRepository.deleteById(id);
            return;
        }
        throw new CouponNotFoundException(id);
        
    }

    public Coupon updateCoupon(long id,Coupon newCoupon){

        // Checking if the Coupon exits
        Optional<Coupon> existingCoupon = getCouponById(id);
        if(existingCoupon.isPresent()){
            Coupon coupon = existingCoupon.get();
            if(coupon.getType() != newCoupon.getType()){
                throw new RuntimeException("Coupon Type can not be updated.");
            }
            coupon.setCLimit(newCoupon.getCLimit());
            coupon.setDetails(newCoupon.getDetails());
            coupon.setStopDate(newCoupon.getStopDate());
            return createCoupon(coupon);

        }else{
            throw new CouponNotFoundException(id);
        }
    }

    public Cart applyCoupon(Coupon coupon, Cart cart){

        // Checking if the coupon is expired or not
        if(coupon.getStopDate() != null && coupon.getStopDate().isBefore(LocalDate.now())){
            throw new CouponExpiredException();
        }

        // Checking if the coupon applicable limit is not crossed
        if(coupon.getCLimit() <= 0){
            throw new CouponLimitExceededException(coupon.getId());
        }

        // Storing product id wise cart items for faster lookup
        Map<Long,CartItem> itemMap = cart.getItems().stream().collect(Collectors.toMap(CartItem::getProductId , cartItem -> cartItem));
        double totalDiscount = calculateCouponWiseDiscount(coupon,cart,itemMap);
        System.out.println("total Discount:"+totalDiscount);
        double totalPrice = cart.calculateTotalPrice();
        double finalPrice = totalPrice - totalDiscount;
        cart.setTotalPrice(totalPrice);
        cart.setTotalDiscount(totalDiscount);
        cart.setFinalPrice(finalPrice);
        coupon.setCLimit(coupon.getCLimit()-1);
        couponRepository.save(coupon);
        return cart;
    }

    public List<DiscountForCoupon> applicableCoupons(Cart cart){
        List<Coupon> couponList = couponRepository.findAll();
        List<DiscountForCoupon> couponDiscounList = new ArrayList<>();

        // Storing product id wise cart items for faster lookup
        Map<Long,CartItem> itemMap = cart.getItems().stream().collect(Collectors.toMap(CartItem::getProductId , cartItem -> cartItem));
        for(Coupon coupon : couponList){
            double totalDicount = 0d;
            if((coupon.getStopDate() == null || coupon.getStopDate().isAfter(LocalDate.now())) && coupon.getCLimit() > 0){
                totalDicount = calculateCouponWiseDiscount(coupon,cart,itemMap);
            }
            couponDiscounList.add(new DiscountForCoupon(coupon.getId(),coupon.getType().getType(),totalDicount));
        }
        
        return couponDiscounList;
    }

    private double calculateCouponWiseDiscount(Coupon coupon, Cart cart, Map<Long,CartItem> itemMap){
        switch (coupon.getType()) {
            case CART_WISE:
                return calculateCartWiseCouponDiscount(coupon,cart);
            case PRODUCT_WISE:
                return calculateProductWiseCouponDiscount(coupon,itemMap);
            case BXGY:
                return calculateBXGYCouponDiscount(coupon,itemMap);
            default:
                throw new RuntimeException("Invalid Coupon Type");
        }
    }

    private double calculateCartWiseCouponDiscount(Coupon coupon, Cart cart){

        // Calculating total cart value
        double totalCartValue = cart.calculateTotalPrice();
        if(totalCartValue >= coupon.getDetails().getThreshold()){
            return totalCartValue * (coupon.getDetails().getDiscount() / 100);
        }
        return 0;
    }

    private double calculateProductWiseCouponDiscount(Coupon coupon, Map<Long,CartItem> cartItemMap){
        CartItem cartItem = cartItemMap.get(coupon.getDetails().getProduct_id());
        if(cartItem != null){
            double product_discount = cartItem.getQuantity() * cartItem.getPrice() * (coupon.getDetails().getDiscount() / 100);
            cartItem.setTotal_discount(product_discount);
            return product_discount; 
        }
        return 0;
    }

    private double calculateBXGYCouponDiscount(Coupon coupon, Map<Long,CartItem> cartItemMap){
        CouponDetails details = coupon.getDetails();
        int repetitionLimit = details.getRepetition_limit();
        
        // Calculate how many times the coupon can be applied based on the "buy" products
        int applicableCount = calculateApplicableCount(details.getBuy_products(), cartItemMap);

        // The coupon can only be applied up to the repetition limit
        int timesApplied = Math.min(repetitionLimit, applicableCount);

        // Now apply the coupon to the "get" products based on how many times it can be applied
        double totalDiscount = 0;
        for (BxGyProducts getProduct : details.getGet_products()) {
            CartItem item = cartItemMap.get(getProduct.getProductId());
            if (item != null) {
                // Calculate the discount based on the number of free items applied
                totalDiscount += item.getPrice() * getProduct.getQuantity() * timesApplied;
            }
        }

        return totalDiscount;
    }

    // Method to calculate how many times the coupon can be applied based on "buy" products
    private int calculateApplicableCount(List<BxGyProducts> buyProducts, Map<Long, CartItem> cartItemMap) {
        int totalBuyQuantity = 0;

        // For each "buy" product in the coupon, calculate how many products are present in the cart
        for (BxGyProducts buyProduct : buyProducts) {
            CartItem cartItem = cartItemMap.get(buyProduct.getProductId());
            if (cartItem != null) {
                // Add the product quantities for "buy" products from the cart
                totalBuyQuantity += cartItem.getQuantity();
            }
        }

        // Check how many complete "buy sets" can be fulfilled
        int buySetSize = buyProducts.get(0).getQuantity();
        int applicableCount = totalBuyQuantity / buySetSize;
        
        return applicableCount;
    }
}
