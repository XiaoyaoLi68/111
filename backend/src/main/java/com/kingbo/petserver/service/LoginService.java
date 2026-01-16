package com.kingbo.petserver.service;

import com.kingbo.petserver.entity.User;

import java.util.Map;

public interface LoginService {
    Map<String, String> accountLogin(User user);
}
