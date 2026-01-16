package com.kingbo.petserver.controller;

import com.kingbo.petserver.entity.User;
import com.kingbo.petserver.service.LoginService;
import com.kingbo.petserver.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/login")
@CrossOrigin
public class LoginController {

    @Autowired
    private LoginService loginService;



    // 平台人员/店铺管理人员/用户登录接口
    @PostMapping("accountLogin")
    public Result accountLogin(@RequestBody User user) {
        // 调用Service层的登录方法
        Map<String, String> maps = loginService.accountLogin(user);
        return Result.success(maps);
    }
}

