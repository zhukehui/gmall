package com.atguigu.gmall.item.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author eternity
 * @create 2019-11-10 10:44
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private GmallWmsClient gmallWmsClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public ItemVO item(Long skuId) {

        ItemVO itemVO = new ItemVO();



        //查询sku信息
        CompletableFuture<SkuInfoEntity> skuCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            BeanUtils.copyProperties(skuInfoEntity, itemVO);
            Long spuId = skuInfoEntity.getSpuId();

            return skuInfoEntity;

        }, threadPoolExecutor);//指定线程池



        CompletableFuture<Void> brandCompletableFuture = skuCompletableFuture.thenAcceptAsync((skuInfoEntity) -> {
            //品牌
            Resp<BrandEntity> brandEntityResp = this.gmallPmsClient.queryBrandById(skuInfoEntity.getBrandId());

            itemVO.setBrand(brandEntityResp.getData());

        }, threadPoolExecutor);//指定线程池


        CompletableFuture<Void> categoryCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //分类
            Resp<CategoryEntity> categoryEntityResp = this.gmallPmsClient.queryCategoryById(skuInfoEntity.getCatalogId());

            itemVO.setCategory(categoryEntityResp.getData());

        }, threadPoolExecutor);//指定线程池


        CompletableFuture<Void> spuCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //spu信息
            Resp<SpuInfoEntity> spuInfoEntityResp = this.gmallPmsClient.querySpuById(skuInfoEntity.getSpuId());

            itemVO.setSpuInfo(spuInfoEntityResp.getData());

        }, threadPoolExecutor);//指定线程池


        CompletableFuture<Void> picCompletableFuture = CompletableFuture.runAsync(() -> {
            //图片信息
            Resp<List<String>> picsResp = this.gmallPmsClient.queryPicsBySkuId(skuId);
            itemVO.setPics(picsResp.getData());

        }, threadPoolExecutor);//指定线程池


        CompletableFuture<Void> saleCompletableFuture = CompletableFuture.runAsync(() -> {
            //营销信息
            Resp<List<ItemSaleVO>> itemSaleResp = this.gmallSmsClient.queryItemSaleVOs(skuId);
            itemVO.setSales(itemSaleResp.getData());

        }, threadPoolExecutor);//指定线程池


        CompletableFuture<Void> storeCompletableFuture = CompletableFuture.runAsync(() -> {
            //是否有货
            Resp<List<WareSkuEntity>> wareSkuResp = this.gmallWmsClient.queryWareSkuBySkuId(skuId);
            List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();
            itemVO.setStore(wareSkuEntities.stream().anyMatch(t -> t.getStock() > 0));

        }, threadPoolExecutor);//指定线程池


        CompletableFuture<Void> spusaleCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //spu所有的销售属性
            Resp<List<SkuSaleAttrValueEntity>> saleAttrValueResp = this.gmallPmsClient.querySaleAttrValues(skuInfoEntity.getSpuId());
            itemVO.setSkuSales(saleAttrValueResp.getData());
        }, threadPoolExecutor);//指定线程池


        CompletableFuture<Void> descCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //spu的描述信息
            Resp<SpuInfoDescEntity> spuInfoDescEntityResp = this.gmallPmsClient.querySpuDescById(skuInfoEntity.getSpuId());

            itemVO.setDesc(spuInfoDescEntityResp.getData());
        }, threadPoolExecutor);//指定线程池


        CompletableFuture<Void> groupCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //规格属性分组及组下的规格参数及值
            Resp<List<GroupVO>> listResp = this.gmallPmsClient.queryGroupVOByCid(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId());

            itemVO.setGroups(listResp.getData());

        }, threadPoolExecutor);//指定线程池


        CompletableFuture.allOf(brandCompletableFuture ,categoryCompletableFuture , spuCompletableFuture ,
                picCompletableFuture ,saleCompletableFuture ,storeCompletableFuture ,spusaleCompletableFuture ,
                descCompletableFuture ,groupCompletableFuture).join();
        return itemVO;
    }

//示例：
    /*public static void main(String[] args) {
        *//*
        *
        * thenApply 方法：当一个线程依赖另一个线程时，获取上一个任务返回的结果，并返回当前任务的返回值。

          thenAccept方法：消费处理结果。接收任务的处理结果，并消费处理，无返回结果。

          thenRun方法：只要上面的任务执行完成，就开始执行thenRun，只是处理完任务后，执行 thenRun的后续操作

          带有Async默认是异步执行的。这里所谓的异步指的是不在当前线程内执行。
        * *//*
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("初始化CompletableFuture对象！！！");
//            int i = 1 / 0 ;
            return "hello";
        }).thenApply(t -> {
            System.out.println("thenApply.......");
            System.out.println("t ====" + t);
            return "thenApply";
        }).whenCompleteAsync((t , u )-> { //使用同一个线程，相当于第一个的子线程
            System.out.println("whenCompleteAsync......");
            System.out.println("t :" + t);
            System.out.println("u :" + u);

        }).exceptionally(t -> { //异常
            System.out.println("exceptionally......");
            System.out.println("t =" + t);
            return "exception";
        }).handle((t , u ) -> { //handle 是在任务完成后再执行，还可以处理异常的任务。
            System.out.println("handle------");
            System.out.println("t ..."+ t);
            System.out.println("u ..." + u);
            return "handle";
        }).thenCombine(CompletableFuture.completedFuture("completedFuture"), (t ,u ) ->{
            System.out.println("t ：" + t + " ,u : " + u );
            System.out.println("两个线程完成后的一个新的业务逻辑");
            return "thenCombine";

            *//**
             * 两个任务必须都完成，触发该任务。
             *
             * thenCombine：组合两个future，获取两个future的返回结果，并返回当前任务的返回值
             *
             * thenAcceptBoth：组合两个future，获取两个future任务的返回结果，然后处理任务，没有返回值。
             *
             * runAfterBoth：组合两个future，不需要获取future的结果，只需两个future处理完任务后，处理该任务。
             *
             *
             *
             * 当两个任务中，任意一个future任务完成的时候，执行任务。
             *
             * applyToEither：两个任务有一个执行完成，获取它的返回值，处理任务并有新的返回值。
             *
             * acceptEither：两个任务有一个执行完成，获取它的返回值，处理任务，没有新的返回值。
             *
             * runAfterEither：两个任务有一个执行完成，不需要获取future的结果，处理任务，也没有返回值。
             *
             *
             *//*
        });

        *//*try {
            System.out.println(completableFuture.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*//*
    }*/
    public static void main(String[] args) {
        /**
         * allOf：等待所有任务完成
         *
         * anyOf：只要有一个任务完成
         */
        List<CompletableFuture<String>> completableFutures = Arrays.asList(CompletableFuture.completedFuture("hello"),
                CompletableFuture.completedFuture("world"),
                CompletableFuture.completedFuture("future"));

        CompletableFuture<Void> future = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[]{}));

        future.whenComplete((t , u ) -> {
            completableFutures.stream().forEach(future1 -> {
                try {
                    System.out.println(future1.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        });

    }
}
