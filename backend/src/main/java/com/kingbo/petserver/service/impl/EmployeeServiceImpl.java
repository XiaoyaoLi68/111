package com.kingbo.petserver.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kingbo.petserver.dao.DepartmentDao;
import com.kingbo.petserver.dao.EmployeeDao;
import com.kingbo.petserver.dao.RoleDao;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Department;
import com.kingbo.petserver.entity.Employee;
import com.kingbo.petserver.entity.Role;
import com.kingbo.petserver.service.EmployeeService;
import com.kingbo.petserver.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * (Employee)表服务实现类
 *
 * @author makejava
 * @since 2026-01-12 09:29:31
 */
@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
    @Resource
    private EmployeeDao employeeDao;

    @Resource
    private RoleDao roleDao;

    @Resource
    private DepartmentDao departmentDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override

    public Employee queryById(Long id) {
        return this.employeeDao.queryById(id);
    }

    @Transactional
//TODO 需要看看性能如何修改
    public Result<PageInfo<Employee>> queryByPage(PageDto pageDto) {
        PageHelper.startPage(pageDto.getCurrentPage(), pageDto.getPageSize());
        List<Employee> employees = employeeDao.count(pageDto.getKeyword());
        for (Employee employee : employees) {
            employee.setPassword("");
            employee.setSalt("");
            employee.setDepartment(departmentDao.queryById(employee.getDepartmentId()));
            List<Long> roleIds = roleDao.queryByEmployeeId(employee.getId());
            List<Role> roleList = new ArrayList<>();
            // 必须判空！防止员工没有任何角色时报错
            if (roleIds != null && !roleIds.isEmpty()) {
                // 循环拿着 ID 去查 Role 对象 (虽然有性能问题，但先保证能跑通)
                for (Long rId : roleIds) {
                    Role r = roleDao.queryById(rId);
                    if (r != null) {
                        roleList.add(r);
                    }
                }
            }

            // 无论有没有查到，都塞进去（有数据是列表，没数据是空列表 []）
            employee.setRoles(roleList);
        }
        return Result.success(new PageInfo<>(employees));
    }

    /**
     * 新增数据
     *
     * @param employee 实例对象
     * @return 实例对象
     */
    @Override
    public Result<Employee> insert(Employee employee) {
        employeeDao.insert(employee);
        return Result.success(employee);
    }

    /**
     * 修改数据
     *
     * @param employee 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> update(Employee employee) {
        // --- 1. 密码处理 ---
        // 修正：必须判断是否为 "空字符串" (hasLength/isNotBlank)，否则前端传 "" 也会被加密
        if (StringUtils.hasLength(employee.getPassword())) {
            UUID uuid = UUID.randomUUID();
            String md5 = SecureUtil.md5(employee.getPassword() + uuid);
            employee.setSalt(uuid.toString());
            employee.setPassword(md5);
        } else {
            // 如果是空字符串，设为 null，防止 MyBatis 的 update 语句误更新密码字段
            employee.setPassword(null);
        }
        if(employee.getId() == null){
            employeeDao.insert(employee);
            insertRoles(employee);
            return Result.success("操作成功");
        }
        // --- 3. 修改逻辑 ---

        // 3.1 部门经理变更逻辑 (比较复杂，建议先查旧数据)
        // 如果要处理 "员工换部门后，自动卸任原部门经理"，必须先查出他在数据库里的旧状态
        Employee oldDbEmployee = employeeDao.queryById(employee.getId());

        // 如果原部门 ID 和新部门 ID 不一样，且他原本是原部门的经理
        if (oldDbEmployee != null && oldDbEmployee.getDepartmentId() != null
                && !oldDbEmployee.getDepartmentId().equals(employee.getDepartmentId())) {

            // 查出旧部门信息
            Department oldDept = departmentDao.queryById(oldDbEmployee.getDepartmentId());
            if (oldDept != null && employee.getId().equals(oldDept.getManagerId())) {
                oldDept.setManagerId(null);
                departmentDao.update(oldDept);
            }
        }
        // 3.2 更新员工基本信息
        employeeDao.update(employee);
        // 策略：不管原来有什么，先全删，再全加 (最稳妥简单的方式)

        // 第一步：删除该员工关联的所有角色(只用执行删除一次，因为删除语句会直接删除所有满足条件的数据库数据)
        roleDao.deleteByEmployeeId(employee.getId());

        // 第二步：如果有新角色，插入
        insertRoles(employee);

        return Result.success("修改成功");
    }
    // 提取公用方法：插入角色
    private void insertRoles(Employee employee) {
        if (employee.getRoles() != null && !employee.getRoles().isEmpty()) {
            for (Role role : employee.getRoles()) {
                // 确保只传 ID，防止对象为空等问题
                if (role != null && role.getId() != null) {
                    roleDao.insertEmployee(employee.getId(), role.getId());
                }
            }
        }
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    @Transactional
    public Result<String> deleteById(Long id) {
        departmentDao.updateNullByManagerId(id);
        roleDao.deleteByEmployee(id);

        if(employeeDao.deleteById(id) > 0){
            return Result.success("操作成功");
        }
        return Result.failure("操作失败");
    }

    @Override
    @Transactional
    public Result<String> deleteByIds(List<Long> ids) {
        departmentDao.updateNullByManagerIds(ids);
        roleDao.deleteByEmployees(ids);

        if(employeeDao.deleteByIds(ids) > 0){
            return Result.success("删除成功");
        }
        return Result.failure("删除失败");
    }

    @Override
    public Result<List<Employee>> queryAll() {
        return Result.success(employeeDao.queryAll());
    }
}
