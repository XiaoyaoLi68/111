package com.kingbo.petserver.service;

import com.github.pagehelper.PageInfo;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Pet;
import com.kingbo.petserver.vo.Result;

/**
 * (Pet)表服务接口
 *
 * @author makejava
 * @since 2026-01-16 10:44:16
 */
public interface PetService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Pet queryById(Long id);

    /**
     * 新增数据
     *
     * @param pet 实例对象
     * @return 实例对象
     */
    Pet insert(Pet pet);

    /**
     * 修改数据
     *
     * @param pet 实例对象
     * @return 实例对象
     */
    Pet update(Pet pet);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    Result<PageInfo<Pet>> queryPage(PageDto pageDto);
}
