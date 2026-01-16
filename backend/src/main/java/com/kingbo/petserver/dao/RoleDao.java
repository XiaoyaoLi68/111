package com.kingbo.petserver.dao;

import com.kingbo.petserver.entity.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * (Role)表数据库访问层
 *
 * @author makejava
 * @since 2026-01-12 19:29:41
 */
public interface RoleDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Role queryById(Long id);

    /**
     * 查询指定行数据
     *
     * @param role     查询条件
     * @param pageable 分页对象
     * @return 对象列表
     */
    List<Role> queryAllByLimit(Role role, @Param("pageable") Pageable pageable);

    /**
     * 统计总行数
     *
     * @param role 查询条件
     * @return 总行数
     */
    long count(Role role);

    /**
     * 新增数据
     *
     * @param role 实例对象
     * @return 影响行数
     */
    int insert(Role role);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Role> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Role> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Role> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
    int insertOrUpdateBatch(@Param("entities") List<Role> entities);

    /**
     * 修改数据
     *
     * @param role 实例对象
     * @return 影响行数
     */
    int update(Role role);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Long id);

    @Select("select * from t_role")
    List<Role> queryAll();


    void insertEmployee(@Param("employeeId") Long employeeId,@Param("rolesId") Long rolesId);

    void updateEmployee(@Param("employeeId") Long employeeId,@Param("rolesId") Long rolesId);

    boolean existByEmployeeId(Long employeeId);

    @Delete("delete from t_employee_role where employee_id = #{id}")
    void deleteByEmployee(Long id);

    List<Long> queryByEmployeeId(Long employeeId);

    void deleteByEmployeeId(Long id);

    void deleteByEmployees(@Param("ids") List<Long> ids);
}

