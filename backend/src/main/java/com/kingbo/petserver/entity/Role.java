package com.kingbo.petserver.entity;

import java.io.Serializable;

/**
 * (Role)实体类
 *
 * @author makejava
 * @since 2026-01-12 19:29:41
 */
public class Role implements Serializable {
    private static final long serialVersionUID = 687120808702062385L;

    private Long id;

    private String name;

    private String sn;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

}

