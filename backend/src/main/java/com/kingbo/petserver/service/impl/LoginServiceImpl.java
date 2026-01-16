package com.kingbo.petserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import com.kingbo.myjwtutilsstarter.MyJwtUtils;
import com.kingbo.petserver.dao.EmployeeDao;
import com.kingbo.petserver.dao.UserDao;
import com.kingbo.petserver.entity.Employee;
import com.kingbo.petserver.entity.User;
import com.kingbo.petserver.exception.PethomeException;
import com.kingbo.petserver.service.LoginService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private EmployeeDao employeeDao;

    @Resource
    private UserDao userDao;

    @Resource
    private MyJwtUtils myJwtUtils;

    // 平台人员/店铺管理人员/用户登录接口
    @Override
    public Map<String, String> accountLogin(User user) {

        // 返回的数据
        HashMap<String, String> hashMap = new HashMap<>();
        String token;
        JSONObject jsonObj = null;
        // 校验参数
        if (!ObjectUtil.isAllNotEmpty(user.getUsername(), user.getPassword(), user.getType())) {
            PethomeException.throwPetException("用户名或密码不能为空");
        }
        // 数据库查询
        // 根据用户名 查询 数据库 返回一个EMP 或者 User对象
        if (user.getType() == 0) {
            // 平台人员/店铺管理人员
            Employee employee = employeeDao.selectEmpByUsername(user.getUsername());
            jsonObj = new JSONObject(employee);
        } else if (user.getType() == 1) {
            // 用户
            User user1 = userDao.selectByUsername(user.getUsername());
            jsonObj = new JSONObject(user1);
        } else {
            PethomeException.throwPetException("登录类型错误");
        }
        // 用户名不存在
        if (ObjectUtil.isEmpty(jsonObj)) {
            PethomeException.throwPetException("用户名或密码错误");
        }
        // 密码校验
        // 使用原来的盐 和 传递过来的密码 重新加密 然后再对比
        if (!StrUtil.equals(SecureUtil.md5(user.getPassword() + jsonObj.getStr("salt")), jsonObj.getStr("password"))) {
            PethomeException.throwPetException("用户名或密码错误");
        }
        // 生成Token
        token = myJwtUtils.createToken(Map.of("id",jsonObj.getStr("id")));
        // 拼接返回值  token loginInfo
        hashMap.put("token", token);
        hashMap.put("loginInfo", jsonObj.toString());
        hashMap.put("permissions", "");
        hashMap.put("menus", "");

        return hashMap;
    }
}

