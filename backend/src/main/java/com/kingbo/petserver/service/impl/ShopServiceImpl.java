package com.kingbo.petserver.service.impl;

import com.kingbo.petserver.entity.Shop;
import com.kingbo.petserver.dao.ShopDao;
import com.kingbo.petserver.service.ShopService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import jakarta.annotation.Resource;

/**
 * (Shop)表服务实现类
 *
 * @author makejava
 * @since 2026-01-12 20:18:44
 */
@Service("shopService")
public class ShopServiceImpl implements ShopService {
    @Resource
    private ShopDao shopDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Shop queryById(Long id) {
        return this.shopDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param shop        筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @Override
    public Page<Shop> queryByPage(Shop shop, PageRequest pageRequest) {
        long total = this.shopDao.count(shop);
        return new PageImpl<>(this.shopDao.queryAllByLimit(shop, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param shop 实例对象
     * @return 实例对象
     */
    @Override
    public Shop insert(Shop shop) {
        this.shopDao.insert(shop);
        return shop;
    }

    /**
     * 修改数据
     *
     * @param shop 实例对象
     * @return 实例对象
     */
    @Override
    public Shop update(Shop shop) {
        this.shopDao.update(shop);
        return this.queryById(shop.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.shopDao.deleteById(id) > 0;
    }
}
