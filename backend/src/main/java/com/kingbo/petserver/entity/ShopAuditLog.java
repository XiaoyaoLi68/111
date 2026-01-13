package com.kingbo.petserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (ShopAuditLog)实体类
 *
 * @author makejava
 * @since 2026-01-13 23:08:34
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopAuditLog implements Serializable {
    private static final long serialVersionUID = -26381218637746263L;

    private Long id;

    private Integer state;

    private Long shopId;

    private Long auditId;

    private Date auditTime;

    private String note;

}

