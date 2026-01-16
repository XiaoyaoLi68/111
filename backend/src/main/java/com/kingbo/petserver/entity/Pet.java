package com.kingbo.petserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * (Pet)实体类
 *
 * @author makejava
 * @since 2026-01-16 10:44:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pet implements Serializable {
    private static final long serialVersionUID = -48056097844570947L;

    private Long id;

    private String name;

    private BigDecimal costprice;

    private Double saleprice;
    /**
     * 类型id
     */
    private Long typeId;

    private String resources;
    /**
     * 状态：0下架 1上架
     */
    private Integer state;

    private Date offsaletime;

    private Date onsaletime;

    private Date createtime;
    /**
     * 店铺Id 如果被领养店铺id为null
     */
    private Long shopId;
    /**
     * 如果被领养为领养用户id
     */
    private Long userId;

    private Long searchMasterMsgId;



}

