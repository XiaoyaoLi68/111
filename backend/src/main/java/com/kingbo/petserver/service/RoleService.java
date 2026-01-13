package com.kingbo.petserver.service;

import com.kingbo.petserver.entity.Role;
import com.kingbo.petserver.vo.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * (Role)表服务接口
 *
 * @author makejava
 * @since 2026-01-12 19:29:41
 */
public interface RoleService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Role queryById(Long id);

    /**
     * 分页查询
     *
     * @param role        筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    Page<Role> queryByPage(Role role, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param role 实例对象
     * @return 实例对象
     */
    Role insert(Role role);

    /**
     * 修改数据
     *
     * @param role 实例对象
     * @return 实例对象
     */
    Role update(Role role);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);

    Result<List<Role>> queryAll();
}
