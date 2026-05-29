package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campustrade.dto.OrderDTO;
import com.campustrade.entity.Goods;
import com.campustrade.entity.Order;
import com.campustrade.mapper.GoodsMapper;
import com.campustrade.mapper.OrderMapper;
import com.campustrade.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final GoodsMapper goodsMapper;

    public OrderServiceImpl(GoodsMapper goodsMapper) {
        this.goodsMapper = goodsMapper;
    }

    @Override
    @Transactional
    public Order create(OrderDTO dto, Long buyerId) {
        Goods goods = goodsMapper.selectById(dto.getGoodsId());
        if (goods == null) throw new RuntimeException("商品不存在");
        if (goods.getStatus() != 1) throw new RuntimeException("商品已下架或已售出");
        if (goods.getUserId().equals(buyerId)) throw new RuntimeException("不能购买自己的商品");

        // 生成订单编号
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 6);

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setGoodsId(dto.getGoodsId());
        order.setBuyerId(buyerId);
        order.setSellerId(goods.getUserId());
        order.setAmount(goods.getPrice());
        order.setStatus(0);   // 待确认
        order.setRemark(dto.getRemark());
        save(order);

        // 更新商品状态为已售出
        goods.setStatus(2);
        goodsMapper.updateById(goods);

        return order;
    }

    @Override
    public Page<Order> myOrders(Integer page, Integer size, Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getBuyerId, userId)
                .orderByDesc(Order::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Page<Order> soldOrders(Integer page, Integer size, Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getSellerId, userId)
                .orderByDesc(Order::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public void updateStatus(Long orderId, Integer status, Long userId) {
        Order order = getById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        // 只允许买家确认收货（0->2）或卖家确认（0->1），或是取消（0/1->3）
        if (status == 2 && !order.getBuyerId().equals(userId))
            throw new RuntimeException("只有买家可以确认收货");
        if (status == 1 && !order.getSellerId().equals(userId))
            throw new RuntimeException("只有卖家可以确认订单");
        order.setStatus(status);
        updateById(order);
    }
}
