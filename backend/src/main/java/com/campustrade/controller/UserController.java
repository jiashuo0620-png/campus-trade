package com.campustrade.controller;

import com.campustrade.common.Result;
import com.campustrade.dto.LoginDTO;
import com.campustrade.dto.RegisterDTO;
import com.campustrade.entity.User;
import com.campustrade.service.UserService;
import com.campustrade.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /** 注册 */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterDTO dto) {
        User user = userService.register(dto);
        user.setPassword(null);
        return Result.ok(user);
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        String token = userService.login(dto);
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        User user = userService.findByUsername(dto.getUsername());
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("role", user.getRole());
        map.put("user", userInfo);
        return Result.ok(map);
    }

    /** 获取个人信息 */
    @GetMapping("/profile")
    public Result<User> profile(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        User user = userService.getById(userId);
        if (user != null) user.setPassword(null);
        return Result.ok(user);
    }

    /** 修改个人信息 */
    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestHeader("Authorization") String token, @RequestBody User param) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        User user = userService.updateProfile(userId, param);
        user.setPassword(null);
        return Result.ok(user);
    }
}
