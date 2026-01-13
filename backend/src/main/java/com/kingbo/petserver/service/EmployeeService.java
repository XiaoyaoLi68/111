package com.kingbo.petserver.service;

import com.github.pagehelper.PageInfo;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Employee;
import com.kingbo.petserver.vo.Result;

import java.util.List;

/**
 * (Employee)表服务接口
 *
 * @author makejava
 * @since 2026-01-12 09:29:31
 */
public interface EmployeeService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Employee queryById(Long id);

    Result<PageInfo<Employee>> queryByPage(PageDto pageDto);

    /**
     * 新增数据
     *
     * @param employee 实例对象
     * @return 实例对象
     */
    Result<Employee> insert(Employee employee);

    /**
     * 修改数据
     *
     * @param employee 实例对象
     * @return 实例对象
     */
    Result<String> update(Employee employee);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    Result<String> deleteById(Long id);

    Result<List<Employee>> queryAll();

    Result<String> deleteByIds(List<Long> ids);
}
