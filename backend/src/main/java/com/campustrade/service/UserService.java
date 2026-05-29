package com.campustrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campustrade.dto.LoginDTO;
import com.campustrade.dto.RegisterDTO;
import com.campustrade.entity.User;

public interface UserService extends IService<User> {
    /** 注册 */
    User register(RegisterDTO dto);
    /** 登录，返回JWT token */
    String login(LoginDTO dto);
    /** 更新个人信息 */
    User updateProfile(Long userId, User user);
    /** 根据用户名查找 */
    User findByUsername(String username);
}
