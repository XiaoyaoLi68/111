package com.kingbo.petserver.controller;

import com.github.pagehelper.PageInfo;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Department;
import com.kingbo.petserver.service.DepartmentService;
import com.kingbo.petserver.vo.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * (Department)表控制层
 *
 * @author makejava
 * @since 2026-01-11 17:12:47
 */
@RestController
@RequestMapping("org/dept")
public class DepartmentController {
    /**
     * 服务对象
     */
    @Resource
    private DepartmentService departmentService;


    @GetMapping("tree")
    public Result<List<Department>> queryChildren() {
        return departmentService.queryChildren();
    }

    /**
     * 新增数据
     *
     * @param department 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<Department> add(Department department) {
        return ResponseEntity.ok(departmentService.insert(department));
    }

    /**
     * 编辑数据
     *
     * @param department 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<Department> edit(Department department) {
        return ResponseEntity.ok(departmentService.update(department));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Long id) {
        return ResponseEntity.ok(departmentService.deleteById(id));
    }



    @PostMapping("queryPage")
    public Result queryByPage(@RequestBody PageDto pagedto){
        return departmentService.queryByPage(pagedto);
    }

}

