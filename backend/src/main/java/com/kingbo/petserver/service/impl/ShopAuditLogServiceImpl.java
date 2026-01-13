package com.kingbo.petserver.service.impl;

import com.kingbo.petserver.entity.ShopAuditLog;
import com.kingbo.petserver.dao.ShopAuditLogDao;
import com.kingbo.petserver.service.ShopAuditLogService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * (ShopAuditLog)表服务实现类
 *
 * @author makejava
 * @since 2026-01-13 23:08:34
 */
@Service("shopAuditLogService")
public class ShopAuditLogServiceImpl implements ShopAuditLogService {
    @Resource
    private ShopAuditLogDao shopAuditLogDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ShopAuditLog queryById(Long id) {
        return this.shopAuditLogDao.queryById(id);
    }


    /**
     * 新增数据
     *
     * @param shopAuditLog 实例对象
     * @return 实例对象
     */
    @Override
    public ShopAuditLog insert(ShopAuditLog shopAuditLog) {
        this.shopAuditLogDao.insert(shopAuditLog);
        return shopAuditLog;
    }

    /**
     * 修改数据
     *
     * @param shopAuditLog 实例对象
     * @return 实例对象
     */
    @Override
    public ShopAuditLog update(ShopAuditLog shopAuditLog) {
        this.shopAuditLogDao.update(shopAuditLog);
        return this.queryById(shopAuditLog.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.shopAuditLogDao.deleteById(id) > 0;
    }
}
