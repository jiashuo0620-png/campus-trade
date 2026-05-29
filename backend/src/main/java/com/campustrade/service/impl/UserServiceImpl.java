package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campustrade.dto.LoginDTO;
import com.campustrade.dto.RegisterDTO;
import com.campustrade.entity.User;
import com.campustrade.mapper.UserMapper;
import com.campustrade.service.UserService;
import com.campustrade.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserServiceImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User register(RegisterDTO dto) {
        // 检查用户名是否已存在
        User exist = findByUsername(dto.getUsername());
        if (exist != null) throw new RuntimeException("用户名已存在");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setRole(0);
        user.setStatus(1);
        save(user);
        return user;
    }

    @Override
    public String login(LoginDTO dto) {
        User user = findByUsername(dto.getUsername());
        if (user == null) throw new RuntimeException("用户名或密码错误");
        if (user.getStatus() == 0) throw new RuntimeException("账号已被禁用");
        if (!encoder.matches(dto.getPassword(), user.getPassword())) throw new RuntimeException("用户名或密码错误");
        return jwtUtil.generateUserToken(user.getId(), user.getUsername());
    }

    @Override
    public User updateProfile(Long userId, User param) {
        User user = getById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        if (param.getPhone() != null) user.setPhone(param.getPhone());
        if (param.getAvatar() != null) user.setAvatar(param.getAvatar());
        updateById(user);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }
}
