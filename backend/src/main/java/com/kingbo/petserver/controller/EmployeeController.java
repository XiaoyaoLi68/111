package com.kingbo.petserver.controller;

import com.kingbo.petserver.entity.Employee;
import com.kingbo.petserver.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * (Employee)表控制层
 *
 * @author makejava
 * @since 2026-01-12 09:29:29
 */
@RestController
@RequestMapping("employee")
public class EmployeeController {
    /**
     * 服务对象
     */
    @Resource
    private EmployeeService employeeService;

    /**
     * 分页查询
     *
     * @param employee    筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<Page<Employee>> queryByPage(Employee employee, PageRequest pageRequest) {
        return ResponseEntity.ok(this.employeeService.queryByPage(employee, pageRequest));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<Employee> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.employeeService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param employee 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<Employee> add(Employee employee) {
        return ResponseEntity.ok(this.employeeService.insert(employee));
    }

    /**
     * 编辑数据
     *
     * @param employee 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<Employee> edit(Employee employee) {
        return ResponseEntity.ok(this.employeeService.update(employee));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Long id) {
        return ResponseEntity.ok(this.employeeService.deleteById(id));
    }

}

