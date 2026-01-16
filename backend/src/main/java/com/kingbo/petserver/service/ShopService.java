package com.kingbo.petserver.service;

import com.github.pagehelper.PageInfo;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Shop;
import com.kingbo.petserver.entity.ShopAuditLog;
import com.kingbo.petserver.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * (Shop)表服务接口
 *
 * @author makejava
 * @since 2026-01-12 20:18:44
 */
public interface ShopService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Shop queryById(Long id);


    Result<PageInfo<Shop>> queryByPage(PageDto pageDto);

    /**
     * 新增数据
     *
     * @param shop 实例对象
     * @return 实例对象
     */
    Shop insert(Shop shop);

    /**
     * 修改数据
     *
     * @param shop 实例对象
     * @return 实例对象
     */
    Result<String> update(Shop shop) throws Exception;

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Result<String> deleteById(Long id);

    Result<String> sendMessage(String phone);

    Result<String> onboarding(Shop shop);

    Result<String> reject(ShopAuditLog shopAuditLog);

    Result<String> pass(ShopAuditLog shopAuditLog);

    Result<String> deleteByIds(List<Long> ids);

    List<Shop> queryData();

    Map<String, List<?>> queryShopGroupState();

    List<Map<String, Object>> queryShopGroupStateForPie();
}
