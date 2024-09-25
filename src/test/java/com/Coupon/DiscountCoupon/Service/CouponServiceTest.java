package com.Coupon.DiscountCoupon.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Coupon.DiscountCoupon.Exception.CouponExpiredException;
import com.Coupon.DiscountCoupon.Exception.CouponLimitExceededException;
import com.Coupon.DiscountCoupon.Model.BxGyProducts;
import com.Coupon.DiscountCoupon.Model.Cart;
import com.Coupon.DiscountCoupon.Model.CartItem;
import com.Coupon.DiscountCoupon.Model.Coupon;
import com.Coupon.DiscountCoupon.Model.CouponDetails;
import com.Coupon.DiscountCoupon.Model.CouponTypes;
import com.Coupon.DiscountCoupon.Repository.CouponRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    private Cart cart;
    private Coupon cartWiseCoupon;
    private Coupon productWiseCoupon;
    private Coupon bxgyCoupon;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a sample cart
        CartItem item1 = new CartItem(1L, 3, 50.0, false); // 3x Product 1
        CartItem item2 = new CartItem(2L, 2, 30.0, false); // 2x Product 2
        cart = new Cart();
        cart.setItems(new ArrayList<>(Arrays.asList(item1, item2)));

        // Create a cart-wise coupon (10% off for cart totals over 100)
        cartWiseCoupon = new Coupon();
        cartWiseCoupon.setId(1L);
        cartWiseCoupon.setType(CouponTypes.CART_WISE);
        CouponDetails cartWiseDetails = new CouponDetails();
        cartWiseDetails.setThreshold(100.0);
        cartWiseDetails.setDiscount(10.0);
        cartWiseCoupon.setDetails(cartWiseDetails);
        cartWiseCoupon.setStopDate(LocalDate.of(2024, 12, 31));
        cartWiseCoupon.setCLimit(5);

        // Create a product-wise coupon (20% off on Product 1)
        productWiseCoupon = new Coupon();
        productWiseCoupon.setId(2L);
        productWiseCoupon.setType(CouponTypes.PRODUCT_WISE);
        CouponDetails productWiseDetails = new CouponDetails();
        productWiseDetails.setProduct_id(1L);
        productWiseDetails.setDiscount(20.0);
        productWiseCoupon.setDetails(productWiseDetails);
        productWiseCoupon.setStopDate(LocalDate.of(2024, 12, 31));
        productWiseCoupon.setCLimit(5);

        // Create a BxGy coupon (buy 2 from [1, 2], get 1 free from [3])
        BxGyProducts buyProduct = new BxGyProducts();
        buyProduct.setProductId(1L);
        buyProduct.setQuantity(2);

        BxGyProducts getProduct = new BxGyProducts();
        getProduct.setProductId(3L);
        getProduct.setQuantity(1);

        bxgyCoupon = new Coupon();
        bxgyCoupon.setId(3L);
        bxgyCoupon.setType(CouponTypes.BXGY);
        CouponDetails bxgyDetails = new CouponDetails();
        bxgyDetails.setBuy_products(Arrays.asList(buyProduct));
        bxgyDetails.setGet_products(Arrays.asList(getProduct));
        bxgyDetails.setRepetition_limit(3);
        bxgyCoupon.setDetails(bxgyDetails);
        bxgyCoupon.setStopDate(LocalDate.of(2024, 12, 31));
        bxgyCoupon.setCLimit(3);
    }

    // Test applying a cart-wise coupon
    @Test
    public void testApplyCartWiseCoupon() {
        cart = couponService.applyCoupon(cartWiseCoupon, cart);
        assertEquals(189.0, cart.getFinalPrice(), "Cart-wise coupon should apply 10% discount");
    }

    // Test applying a product-wise coupon
    @Test
    public void testApplyProductWiseCoupon() {
        cart = couponService.applyCoupon(productWiseCoupon, cart);
        CartItem item1 = cart.findItemByProductId(1L);
        assertEquals(30.0, item1.getTotal_discount(), "Product-wise coupon should apply 20% discount on Product 1");
    }

    // Test applying a BxGy coupon
    @Test
    public void testApplyBxGyCoupon() {
        CartItem freeItem = new CartItem(3L, 1, 0.0, true); // Free Product 3
        cart.addItem(freeItem);

        cart = couponService.applyCoupon(bxgyCoupon, cart);
        CartItem item3 = cart.findItemByProductId(3L);
        assertEquals(1, item3.getQuantity(), "BxGy coupon should apply 1 free item from the get array");
    }

    // Test coupon expiration
    @Test
    public void testCouponExpiredException() {
        // Set expired date
        cartWiseCoupon.setStopDate(LocalDate.of(2020, 1, 1));

        Exception exception = assertThrows(CouponExpiredException.class, () -> {
            couponService.applyCoupon(cartWiseCoupon, cart);
        });

        String expectedMessage = "Coupon Expired!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    // Test coupon limit exceeded exception
    @Test
    public void testCouponLimitExceededException() {
        // Set limit to 0
        productWiseCoupon.setCLimit(0);

        Exception exception = assertThrows(CouponLimitExceededException.class, () -> {
            couponService.applyCoupon(productWiseCoupon, cart);
        });

        String expectedMessage = "Limit exceeded for coupon with id="+productWiseCoupon.getId();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
