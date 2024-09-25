# discount-coupon-management
This is a Spring Boot application for managing and applying different types of coupons (Cart-wise, Product-wise, BxGy). The system ensures the correct discount logic is applied and handles expiration dates and usage limits for coupons.

## Implemented Cases

## 1. Cart-wise Coupons
    *Description:* 
        A percentage discount is applied to the total cart value if it meets the required threshold.
    *Example:*
        * Coupon: 10% off for carts with a total value of over Rs 100.
        * If a cart has a total value of Rs 150, a discount of Rs 15 is applied, resulting in a final price of Rs 135.
## 2. Product-wise Coupons
    *Description:* 
        A percentage discount is applied to a specific product in the cart.
    *Example:*
        * Coupon: 20% off on Product 1.
        * If Product 1 costs RS 50 and you have 3 units in the cart, the total discount would be Rs 30 (3 * Rs 50 * 20%).
## 3. BxGy (Buy X, Get Y Free) Coupons
    *Description:* 
        Buy a specified number of products from one array (buy array) and get a specified number of products from another array (get array) for free. A repetition limit is also supported.
    *Example:*
        Coupon: 
        * b2g1 (Buy 2 products from [X, Y, Z], get 1 product from [A, B, C] free).
        * If a cart has 2 products from [X, Y, Z] and 1 product from [A, B, C], the product from the "get" array (e.g., A) will be free.
        * Repetition Limit: If the cart has 6 products from [X, Y, Z], and 3 products from [A, B, C], the coupon can be applied 3 times.
## 4. Coupon Expiration Handling
    *Description:* 
        Coupons that have expired cannot be applied, and an exception will be thrown if an expired coupon is attempted to be used.
    *Example:* 
        * If a coupon has an expiration date of 2022-12-31, it will not be valid in 2023.
## 5. Coupon Usage Limits
    *Description:* 
        Each coupon has a maximum usage limit. Once the limit is reached, the coupon cannot be applied anymore.
    *Example:*
        * If a coupon has a limit of 5 uses, after being applied 5 times, it cannot be used again. An exception will be thrown if the limit is exceeded.


## Unimplemented Cases

## 1. Complex Rule-Based Coupons
        The system doesn't support complex rule-based coupons like "Buy X and get 50% off on Y if Z is in the cart". A rule engine can be made to tacke this kind of scenarios.
## 2. Multiple Coupons
        This sytem does not support application of multiple coupons together on the same cart.
## 3. Coupon Conflict/ Overlap
        If multiple coupons are applicable on the same product the which coupon would get higher priorityis beyond the scope of this project.
## 4. No Real-Time Concurrency Handling
        If multiple users attempt to use the same coupon simultaneously and the coupon has a limited usage count, there might be concurrency issues leading to over-application of the coupon. A transactional locking can be implemented to tackle those issues.
## 5. Limited Analytics or Reporting
        The system does not track coupon usage analytics (e.g., how often coupons are used, total discounts provided). There’s no reporting module for business insights.
## 6. Fixed Discounts:
        There can be fixed discounts too. In this project only percentage discount is considered.


## Assumptions

## 1. Products are valid
        while applying coupons to the products we are not validating the products as we are not using any product database. We are assuming all products are valid.
## 2. BxGy Coupons are Applied Based on Exact Quantities
        For BxGy coupons, the assumption is that the cart must have the exact quantities of the buy products to trigger the free get products. Partial quantities (e.g., buying less than required) do not trigger the coupon.
## 3. Expired Coupons are Validated at Application
        The system assumes that a coupon’s validity (expiration date) is only checked at the time of application, not during creation or listing of available coupons.
## 4. Coupon Usage Limits are Decremented After Application
        The system assumes that the usage limit for each coupon is decremented after it is successfully applied to the cart.


