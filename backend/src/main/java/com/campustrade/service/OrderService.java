package com.campustrade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campustrade.dto.OrderDTO;
import com.campustrade.entity.Order;

public interface OrderService extends IService<Order> {
    /** 创建订单 */
    Order create(OrderDTO dto, Long buyerId);
    /** 我的订单（买家视角） */
    Page<Order> myOrders(Integer page, Integer size, Long userId);
    /** 卖出的订单（卖家视角） */
    Page<Order> soldOrders(Integer page, Integer size, Long userId);
    /** 修改订单状态 */
    void updateStatus(Long orderId, Integer status, Long userId);
}
