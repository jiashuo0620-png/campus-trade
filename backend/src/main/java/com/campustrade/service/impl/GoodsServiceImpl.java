package com.campustrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campustrade.dto.GoodsDTO;
import com.campustrade.entity.Goods;
import com.campustrade.mapper.GoodsMapper;
import com.campustrade.service.GoodsService;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Override
    public Goods publish(GoodsDTO dto, Long userId) {
        Goods goods = new Goods();
        goods.setTitle(dto.getTitle());
        goods.setDescription(dto.getDescription());
        goods.setPrice(dto.getPrice());
        goods.setOriginalPrice(dto.getOriginalPrice());
        goods.setCategory(dto.getCategory());
        goods.setImages(dto.getImages());
        goods.setUserId(userId);
        goods.setStatus(0);   // 待审核
        goods.setViewCount(0);
        save(goods);
        return goods;
    }

    @Override
    public Page<Goods> pageList(Integer page, Integer size, String category, String keyword) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, 1)   // 只查在售的
                .orderByDesc(Goods::getCreateTime);
        if (category != null && !category.isEmpty()) wrapper.eq(Goods::getCategory, category);
        if (keyword != null && !keyword.isEmpty()) wrapper.like(Goods::getTitle, keyword);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Goods detail(Long id) {
        Goods goods = getById(id);
        if (goods == null) throw new RuntimeException("商品不存在");
        baseMapper.incrementViewCount(id);
        goods.setViewCount(goods.getViewCount() + 1);
        return goods;
    }

    @Override
    public void deleteGoods(Long id, Long userId) {
        Goods goods = getById(id);
        if (goods == null) throw new RuntimeException("商品不存在");
        if (!goods.getUserId().equals(userId)) throw new RuntimeException("无权删除他人商品");
        removeById(id);
    }

    @Override
    public Page<Goods> myGoods(Integer page, Integer size, Long userId) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getUserId, userId)
                .orderByDesc(Goods::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public void audit(Long id, Integer status) {
        Goods goods = getById(id);
        if (goods == null) throw new RuntimeException("商品不存在");
        goods.setStatus(status);
        updateById(goods);
    }
}
