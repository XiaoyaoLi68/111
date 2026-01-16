package com.kingbo.petserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 164668198422111443L;

    private Long id;

    private String username;

    private String phone;

    private String email;
    /**
     * 盐值
     */
    private String salt;
    /**
     * 密码，md5加密加盐
     */
    private String password;
    /**
     * 员工状态 - 1启用，0禁用
     */
    private Integer state;

    private Integer age;

    private Date createtime;

    private String headimg;

    private Integer Type;


}

