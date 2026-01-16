package com.kingbo.petserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.kingbo.petserver.dao.ShopDao;
import com.kingbo.petserver.entity.Shop;
import com.kingbo.petserver.utils.SpringContextHolder;

import java.util.ArrayList;
import java.util.List;


public class ImportShopListener extends AnalysisEventListener<Shop> {

    // 从容器中获取Mapper的对象

    // 每隔100条存储数据库 然后清理list
    private static final int BATCH_COUNT = 100;
    // 缓存的数据 然后集中存储到数据库中
    private List<Shop> cachedDataList = new ArrayList<>(BATCH_COUNT);

    // EasyExcel每读取一行数据就乎调用一次invoke方法
    // data 数据 就是读取到的每一行数据 EasyExcel很智能 已经帮我们封装到对象中了
    // 如果我们每接收一行数据 就往数据库中保存一下 那么数据库的压力会很大
    // 通常我们会把一批数据 集中保存到数据中 减少和数据库的连接次数
    @Override
    public void invoke(Shop data, AnalysisContext context) {
        // 每读取一条数据 我们先把这个数据放到缓存的list中
        cachedDataList.add(data);
        // 当缓存的数据达到一定数量 就存储到数据库 然后清理list
        if (cachedDataList.size() >= BATCH_COUNT) {
            //  存储到数据库中
            saveData();
            // 把已经存储到数据库的数据清理掉
            cachedDataList.clear();
        }
    }

    // 所有数据解析完成后 会调用这个方法
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 解析完成后 还要把缓存中剩余的数据存储到数据库
        saveData();
    }

    // 把缓存的数据存储到数据库中
    private void saveData() {
        // 调用Dao存储到数据库中
        // 把cachedDataList中的数据存储到数据库中
        ShopDao shopDao = SpringContextHolder.getBean(ShopDao.class);
        shopDao.insertBatch(cachedDataList);
    }
}

