package com.campustrade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campustrade.common.Result;
import com.campustrade.entity.Admin;
import com.campustrade.entity.Goods;
import com.campustrade.entity.User;
import com.campustrade.mapper.AdminMapper;
import com.campustrade.service.GoodsService;
import com.campustrade.service.UserService;
import com.campustrade.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final GoodsService goodsService;
    private final AdminMapper adminMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminController(UserService userService, GoodsService goodsService,
                           AdminMapper adminMapper, JwtUtil jwtUtil) {
        this.userService = userService;
        this.goodsService = goodsService;
        this.adminMapper = adminMapper;
        this.jwtUtil = jwtUtil;
    }

    /** 管理员登录 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        Admin admin = adminMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, username));
        if (admin == null || !encoder.matches(password, admin.getPassword()))
            throw new RuntimeException("用户名或密码错误");
        if (admin.getStatus() == 0) throw new RuntimeException("账号已被禁用");
        String token = jwtUtil.generateAdminToken(admin.getId(), admin.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        return Result.ok(map);
    }

    /** 用户列表 */
    @GetMapping("/users")
    public Result<Page<User>> userList(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        checkAdmin(token);
        Page<User> result = userService.page(new Page<>(page, size));
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.ok(result);
    }

    /** 禁用/启用用户 */
    @PutMapping("/users/{id}/status")
    public Result<Void> userStatus(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id, @RequestParam Integer status) {
        checkAdmin(token);
        User user = userService.getById(id);
        if (user != null) { user.setStatus(status); userService.updateById(user); }
        return Result.ok();
    }

    /** 商品审核 */
    @PutMapping("/goods/{id}/audit")
    public Result<Void> auditGoods(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id, @RequestParam Integer status) {
        checkAdmin(token);
        goodsService.audit(id, status);
        return Result.ok();
    }

    /** 待审核商品列表 */
    @GetMapping("/goods/pending")
    public Result<Page<Goods>> pendingGoods(@RequestHeader("Authorization") String token,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size) {
        checkAdmin(token);
        Page<Goods> result = goodsService.page(new Page<>(page, size),
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Goods>()
                        .eq(Goods::getStatus, 0).orderByDesc(Goods::getCreateTime));
        return Result.ok(result);
    }

    private void checkAdmin(String token) {
        String role = jwtUtil.getRole(token.replace("Bearer ", ""));
        if (!"admin".equals(role)) throw new RuntimeException("无管理员权限");
    }
}
