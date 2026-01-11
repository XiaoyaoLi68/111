package com.kingbo.petserver.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Department;
import com.kingbo.petserver.dao.DepartmentDao;
import com.kingbo.petserver.service.DepartmentService;
import com.kingbo.petserver.vo.Result;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (Department)表服务实现类
 *
 * @author makejava
 * @since 2026-01-11 17:12:48
 */
@Service("departmentService")
public class DepartmentServiceImpl implements DepartmentService {
    @Resource
    private DepartmentDao departmentDao;

    /**
     * 通过ID查询单条数据
     *
     * @return 实例对象
     */
    @Override
    public Result<List<Department>> queryChildren() {
        List<Department> list = departmentDao.count(null);
        Map<Long, Department> departmentMap = new HashMap<>();
        for (Department department : list) {
            departmentMap.put(department.getId(),department);

        }
        List<Department> root = new ArrayList<>();
        for (Department department : list) {
            if(department.getParentId() != null){
                Department parent = departmentMap.get(department.getParentId());
                parent.getChildren().add(department);
            }else root.add(department);
        }
        return Result.success(root);
    }



    @Override
    public Result<PageInfo> queryByPage(PageDto pagedto) {
        PageHelper.startPage(pagedto.getCurrentPage(), pagedto.getPageSize());
        List<Department> list = departmentDao.count(pagedto.getKeyword());
        return Result.success(new PageInfo<>(list));
    }

    /**
     * 新增数据
     *
     * @param department 实例对象
     * @return 实例对象
     */
    @Override
    public Department insert(Department department) {
        this.departmentDao.insert(department);
        return department;
    }

    /**
     * 修改数据
     *
     * @param department 实例对象
     * @return 实例对象
     */
    @Override
    public Department update(Department department) {
        this.departmentDao.update(department);
        return new Department();
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.departmentDao.deleteById(id) > 0;
    }
}
