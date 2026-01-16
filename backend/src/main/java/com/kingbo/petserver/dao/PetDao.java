package com.kingbo.petserver.dao;

import com.kingbo.petserver.entity.Pet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (Pet)表数据库访问层
 *
 * @author makejava
 * @since 2026-01-16 10:44:16
 */
public interface PetDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Pet queryById(Long id);

    /**
     * 统计总行数
     *
     * @param pet 查询条件
     * @return 总行数
     */
    long count(Pet pet);

    /**
     * 新增数据
     *
     * @param pet 实例对象
     * @return 影响行数
     */
    int insert(Pet pet);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Pet> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Pet> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Pet> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Pet> entities);

    /**
     * 修改数据
     *
     * @param pet 实例对象
     * @return 影响行数
     */
    int update(Pet pet);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    List<Pet> queryAllByLimit(String keyword);
}

