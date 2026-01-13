package com.kingbo.petserver.controller;

import com.kingbo.petserver.entity.ShopAuditLog;
import com.kingbo.petserver.service.ShopAuditLogService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * (ShopAuditLog)表控制层
 *
 * @author makejava
 * @since 2026-01-13 23:08:34
 */
@RestController
@RequestMapping("shopAuditLog")
public class ShopAuditLogController {
    /**
     * 服务对象
     */
    @Resource
    private ShopAuditLogService shopAuditLogService;


}

