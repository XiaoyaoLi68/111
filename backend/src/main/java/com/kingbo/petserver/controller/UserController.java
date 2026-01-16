package com.kingbo.petserver.controller;

import com.kingbo.petserver.entity.User;
import com.kingbo.petserver.service.UserService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * (User)表控制层
 *
 * @author makejava
 * @since 2026-01-15 22:33:53
 */
@RestController
@RequestMapping("user")
public class UserController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;


}

