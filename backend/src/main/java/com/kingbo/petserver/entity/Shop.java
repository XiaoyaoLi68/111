package com.kingbo.petserver.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * (Shop)实体类
 *
 * @author makejava
 * @since 2026-01-12 20:18:44
 */
public class Shop implements Serializable {
    private static final long serialVersionUID = 301212293811143034L;

    private Long id;
    /**
     * 店铺名称
     */
    private String name;
    /**
     * 店铺座机
     */
    private String tel;
    /**
     * 入驻时间
     */
    private Date registerTime;
    /**
     * 店铺状态：1待审核，2审核通过待激活，3激活成功，4驳回
     */
    private Integer state;
    /**
     * 店铺地址
     */
    private String address;
    /**
     * 店铺logo
     */
    private String logo;
    /**
     * 店铺管理员ID
     */
    private Long adminId;


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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

}

