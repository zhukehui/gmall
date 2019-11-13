package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.vo.Cart;

import java.util.List;

/**
 * @author eternity
 * @create 2019-11-13 17:26
 */
public interface CartService {

    void addCart(Cart cart);

    List<Cart> queryCarts();

    void updateCart(Cart cart);

    void deleteCart(Long skuId);

    void checkCart(List<Cart> carts);
}
