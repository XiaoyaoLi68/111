package com.kingbo.petserver.controller;

import com.github.pagehelper.PageInfo;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Employee;
import com.kingbo.petserver.service.EmployeeService;
import com.kingbo.petserver.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (Employee)表控制层
 *
 * @author makejava
 * @since 2026-01-12 09:29:29
 */
@RestController
@RequestMapping("org/employee")
public class EmployeeController {
    /**
     * 服务对象
     */
    @Resource
    private EmployeeService employeeService;


    @GetMapping
    public Result<List<Employee>> queryAllEmployee(){
        return employeeService.queryAll();
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<Employee> queryById(@PathVariable Long id) {
        return ResponseEntity.ok(this.employeeService.queryById(id));
    }
    /**
     * 编辑数据
     *
     * @param employee 实体
     * @return 编辑结果
     */
    @PutMapping
    public Result<String> edit(@RequestBody Employee employee) {
        return employeeService.update(employee);
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("{id}")
    public Result<String> deleteById(@PathVariable Long id) {
        return employeeService.deleteById(id);
    }


    @GetMapping("queryAll")
    public Result<List<Employee>> queryAll(){
        return employeeService.queryAll();
    }

    @PostMapping("queryPage")
    public Result<PageInfo<Employee>> queryByPage(@RequestBody PageDto pageDto){
        return employeeService.queryByPage(pageDto);
    }

    @DeleteMapping("batch")
    public Result<String> deleteByIds(@RequestBody List<Long> ids){
        return employeeService.deleteByIds(ids);
    }
}

