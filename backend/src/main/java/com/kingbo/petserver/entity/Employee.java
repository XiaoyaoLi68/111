package com.kingbo.petserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * (Employee)实体类
 *
 * @author makejava
 * @since 2026-01-12 09:29:30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee implements Serializable {
    private static final long serialVersionUID = 832780249422182468L;
    /**
     * 员工ID
     */
    private Long id;
    /**
     * 员工名称
     */
    private String username;
    /**
     * 员工电话
     */
    private String phone;
    /**
     * 员工邮箱
     */
    private String email;
    /**
     * 盐值
     */
    private String salt;
    /**
     * 密码
     */
    private String password;


    private String comfirmPassword;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 员工状态 - 1启用，0禁用
     */
    private Integer state;
    /**
     * 所属部门ID
     */
    private Long departmentId;
    /**
     * 店铺ID
     */
    private Long shopId;

    private Department department;

    private Shop shop;

    private List<Role> roles;

    private Integer verifyCode;
}

