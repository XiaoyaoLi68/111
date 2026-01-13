package com.kingbo.petserver.service;

import com.kingbo.petserver.entity.ShopAuditLog;

/**
 * (ShopAuditLog)表服务接口
 *
 * @author makejava
 * @since 2026-01-13 23:08:34
 */
public interface ShopAuditLogService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ShopAuditLog queryById(Long id);

    /**
     * 新增数据
     *
     * @param shopAuditLog 实例对象
     * @return 实例对象
     */
    ShopAuditLog insert(ShopAuditLog shopAuditLog);

    /**
     * 修改数据
     *
     * @param shopAuditLog 实例对象
     * @return 实例对象
     */
    ShopAuditLog update(ShopAuditLog shopAuditLog);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

}
