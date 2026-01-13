package com.kingbo.petserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * (Shop)实体类
 *
 * @author makejava
 * @since 2026-01-12 20:18:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    private Employee admin;

    private Double latitude;

    private Double longitude;




}

