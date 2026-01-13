package com.kingbo.petserver.service;

import com.github.pagehelper.PageInfo;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Department;
import com.kingbo.petserver.vo.Result;

import java.util.List;

/**
 * (Department)表服务接口
 *
 * @author makejava
 * @since 2026-01-11 17:12:48
 */
public interface DepartmentService {

    /**
     * 通过ID查询单条数据
     *
     * @return 实例对象
     */
    Result<List<Department>> queryChildren();


    Result<PageInfo<Department>> queryByPage(PageDto pagedto);

    /**
     * 新增数据
     *
     * @param department 实例对象
     * @return 实例对象
     */
    Department insert(Department department);

    /**
     * 修改数据
     *
     * @param department 实例对象
     * @return 实例对象
     */
    Department update(Department department);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);



    Result<String> save(Department department);

    Result<String> deleteByIds(List<Long> ids);
}
