package com.atguigu.gmall.cart.controller;



import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.vo.Cart;
import com.atguigu.gmall.cart.vo.CartItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author eternity
 * @create 2019-11-13 17:25
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/order/{userId}")
    public Resp<List<CartItemVO>> queryCartItemVO(@PathVariable("userId")Long userId){

        List<CartItemVO> itemVOListthis = this.cartService.queryCartItemVO(userId);

        return Resp.ok(itemVOListthis);
    }

    /**
     * 新增购物车
     * @param cart
     * @return
     */
    @PostMapping
    public Resp<Object> addCart(@RequestBody Cart cart) {

        this.cartService.addCart(cart);

        return Resp.ok(null);
    }

    /**
     * 查询
     * @return
     */
    @GetMapping
    public Resp<List<Cart>> queryCarts(){

        List<Cart> carts = this.cartService.queryCarts();

        return Resp.ok(carts);
    }

    /**
     * 更新
     * @param cart
     * @return
     */
    @PostMapping("/update")
    public Resp<Object> updateCart(@RequestBody Cart cart){

        this.cartService.updateCart(cart);

        return Resp.ok(null);
    }

    /**
     * 删除
     * @param skuId
     * @return
     */
    @PostMapping("{skuId}")
    public Resp<Object> deleteCart(@PathVariable("skuId")Long skuId){

        this.cartService.deleteCart(skuId);

        return Resp.ok(null);
    }


    /**
     * 选中状态
     * @param carts
     * @return
     */
    @PostMapping("check")
    public Resp<Object> checkCart(@RequestBody List<Cart> carts){

        this.cartService.checkCart(carts);
        return Resp.ok(null);
    }
}
