package com.atguigu.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.vo.Cart;
import com.atguigu.gmall.cart.vo.CartItemVO;
import com.atguigu.gmall.cart.vo.UserInfo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author eternity
 * @create 2019-11-13 17:27
 */
@Service
public class CartServiceImpl implements CartService {

    public static final String KEY_PREFIX = "cart:key:";

    public static final String CURRENT_PRICE_PREFIX = "cart:price:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallSmsClient gmallSmsClient;

    /**
     * 新增购物车
     * @param cart
     */
    @Override
    public void addCart(Cart cart) {

        //判断登录状态
        String key = getKey();

        //判断购物车中是否有该记录
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        String skuId = cart.getSkuId().toString();//skuId
        //取出用户新增购物车商品的数量
        Integer count = cart.getCount();

        //有的话只更新数量
        if (hashOps.hasKey(cart.getSkuId().toString())){

            String cartJson = hashOps.get(skuId).toString();
            //反序列化
           cart = JSON.parseObject(cartJson, Cart.class);
            //更新数量
           cart.setCount(cart.getCount()+count);

        }else {
            //没有新增记录
            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(cart.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            cart.setTitle(skuInfoEntity.getSkuTitle());//设置标题
            cart.setCheck(true);//设置是否选中(默认为选中)
            cart.setPrice(skuInfoEntity.getPrice());//设置价格
            //查询销售属性
            Resp<List<SkuSaleAttrValueEntity>> listResp = this.gmallPmsClient.querySaleAttrBySkuId(cart.getSkuId());
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = listResp.getData();
            cart.setSkuAttrValue(skuSaleAttrValueEntities);//设置商品规格参数
            cart.setDefaultImage(skuInfoEntity.getSkuDefaultImg());//设置图片
            Resp<List<ItemSaleVO>> listResp1 = this.gmallSmsClient.queryItemSaleVOs(cart.getSkuId());
            cart.setSales(listResp1.getData());//设置营销信息
            this.stringRedisTemplate.opsForValue().set(CURRENT_PRICE_PREFIX+ skuId ,skuInfoEntity.getPrice().toString());
        }
        //同步到Redis中
        hashOps.put(skuId,JSON.toJSONString(cart));
    }

    /**
     * 查询
     * @return
     */
    @Override
    public List<Cart> queryCarts() {

        //判断登录状态
        // 查询未登录状态的购物车
        UserInfo userInfo = LoginInterceptor.get();
        String key1 = KEY_PREFIX + userInfo.getUserKey();

        BoundHashOperations<String, Object, Object> userKeyOps = this.stringRedisTemplate.boundHashOps(key1);// 获取未登录状态的购物车
        List<Object> cartJsonList = userKeyOps.values();
        List<Cart> userKeyCarts = null;
        if (!CollectionUtils.isEmpty(cartJsonList)){
           userKeyCarts = cartJsonList.stream().map(cartJson -> {
               Cart cart = JSON.parseObject(cartJson.toString(), Cart.class);
               cart.setCurrentPrice(new BigDecimal(this.stringRedisTemplate.opsForValue().get(CURRENT_PRICE_PREFIX+cart.getSkuId())));
               return cart;
           }).collect(Collectors.toList());
        }

        //判断登录状态
        if (userInfo.getUserId() == null){
            //未登录直接返回
            return userKeyCarts;

        }

        //登录，查询登录状态的购物车
        String key2 = KEY_PREFIX + userInfo.getUserId();// 用户已登录，查询登录状态的购物车
        BoundHashOperations<String, Object, Object> userIdOps = this.stringRedisTemplate.boundHashOps(key2);// 获取登录状态的购物车

        //判断未登录的购物车是否为空
        if (!CollectionUtils.isEmpty(userKeyCarts)){
            // 如果未登录状态的购物车不为空，需要合并
            userKeyCarts.forEach(cart -> {
                //有的话只更新数量
                if (userIdOps.hasKey(cart.getSkuId().toString())){

                    String cartJson = userIdOps.get(cart.getSkuId().toString()).toString();//反序列化
                    Cart idCart = JSON.parseObject(cartJson, Cart.class);
                    //更新数量
                    idCart.setCount(idCart.getCount() + cart.getCount());
                    userIdOps.put(cart.getSkuId().toString(), JSON.toJSONString(idCart));
                }else {
                    //没有新增记录
                   userIdOps.put(cart.getSkuId().toString(), JSON.toJSONString(cart));
                }
            });
            //合并完成后，删除未登录的购物车
            this.stringRedisTemplate.delete(key1);
        }
        //查询返回
        List<Object> userIdCartJsonList = userIdOps.values();
        if (CollectionUtils.isEmpty(userIdCartJsonList)){
            return null;
        }
        return userIdCartJsonList.stream().map(userIdCartJson -> {
            Cart cart = JSON.parseObject(userIdCartJson.toString(), Cart.class);
            cart.setCurrentPrice(new BigDecimal(this.stringRedisTemplate.opsForValue().get(CURRENT_PRICE_PREFIX+cart.getSkuId())));
            return cart;

        }).collect(Collectors.toList());

    }

    /**
     * 更新
     * @param cart
     */
    @Override
    public void updateCart(Cart cart) {
        // 获取redis的key
        String key = getKey();

        Integer count = cart.getCount();

        // 获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        if (hashOps.hasKey(cart.getSkuId().toString())){
            //获取购物车中的更新数量的购物记录
            String cartJson = hashOps.get(cart.getSkuId().toString()).toString();
            cart = JSON.parseObject(cartJson, Cart.class);
            cart.setCount(count);

           hashOps.put(cart.getSkuId().toString(), JSON.toJSONString(cart));
        }
    }

    /**
     * 删除
     * @param skuId
     */
    @Override
    public void deleteCart(Long skuId) {

        // 获取redis的key
        String key = getKey();

        // 获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        if (hashOps.hasKey(skuId.toString())){
            hashOps.delete(skuId.toString());
        }

    }

    /**
     * 修改选中状态
     * @param carts
     */
    @Override
    public void checkCart(List<Cart> carts) {
        // 获取redis的key
        String key = getKey();

        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);

        carts.forEach(cart -> {
            Boolean check = cart.getCheck();
            if (hashOps.hasKey(cart.getSkuId().toString())){
                //获取购物车中的更新数量的购物记录
                String cartJson = hashOps.get(cart.getSkuId().toString()).toString();
                cart = JSON.parseObject(cartJson, Cart.class);
                cart.setCheck(check);//更新选中状态

                hashOps.put(cart.getSkuId().toString(), JSON.toJSONString(cart));
            }
        });

    }

    @Override
    public List<CartItemVO> queryCartItemVO(Long userId) {
        //登录，查询登录状态的购物车
        String key = KEY_PREFIX + userId;// 用户已登录，查询登录状态的购物车
        BoundHashOperations<String, Object, Object> userIdOps = this.stringRedisTemplate.boundHashOps(key);// 获取登录状态的购物车


        //查询返回
        List<Object> userIdCartJsonList = userIdOps.values();
        if (CollectionUtils.isEmpty(userIdCartJsonList)){
            return null;
        }
        //获取所有的购物车记录
        return userIdCartJsonList.stream().map(userIdCartJson -> {
            Cart cart = JSON.parseObject(userIdCartJson.toString(), Cart.class);
            cart.setCurrentPrice(new BigDecimal(this.stringRedisTemplate.opsForValue().get(CURRENT_PRICE_PREFIX + cart.getSkuId())));
            return cart;

        }).filter(cart -> cart.getCheck()).map(cart -> {
            CartItemVO cartItemVO = new CartItemVO();
            cartItemVO.setSkuId(cart.getSkuId());
            cartItemVO.setCount(cart.getCount());
            return cartItemVO;
        }).collect(Collectors.toList());

    }

    private String getKey() {
        String key = KEY_PREFIX;
        //判断登录状态
        UserInfo userInfo = LoginInterceptor.get();
        if (userInfo.getUserId() != null){

            key += userInfo.getUserId();

        }else {
            key += userInfo.getUserKey();
        }
        return key;
    }
}
