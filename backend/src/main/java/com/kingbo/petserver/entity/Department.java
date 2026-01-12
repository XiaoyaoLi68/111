package com.kingbo.petserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * (Department)实体类
 *
 * @author makejava
 * @since 2026-01-11 17:12:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department implements Serializable {
    private static final long serialVersionUID = 953818530538352700L;

    private Long id;
    /**
     * 部门编号
     */
    private String sn;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 部门的上级分类层级id
     */
    private String dirPath;
    /**
     * 部门状态 - 1启用，0禁用
     */
    private Integer state;
    /**
     * 部门管理员，关联Employee表id
     */
    private Long managerId;
    /**
     * 上级部门
     */
    private Long parentId;

    private Department parent;

    private Employee manager;

    private List<Department> children = new ArrayList<>();

}

