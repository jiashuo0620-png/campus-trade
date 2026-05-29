package com.campustrade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campustrade.common.Result;
import com.campustrade.dto.OrderDTO;
import com.campustrade.entity.Order;
import com.campustrade.service.OrderService;
import com.campustrade.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    /** 创建订单 */
    @PostMapping
    public Result<Order> create(@RequestHeader("Authorization") String token, @Valid @RequestBody OrderDTO dto) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.ok(orderService.create(dto, userId));
    }

    /** 我的订单（买家） */
    @GetMapping("/buy")
    public Result<Page<Order>> buyOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.ok(orderService.myOrders(page, size, userId));
    }

    /** 卖出的订单（卖家） */
    @GetMapping("/sell")
    public Result<Page<Order>> sellOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.ok(orderService.soldOrders(page, size, userId));
    }

    /** 修改订单状态 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestParam Integer status) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        orderService.updateStatus(id, status, userId);
        return Result.ok();
    }
}
