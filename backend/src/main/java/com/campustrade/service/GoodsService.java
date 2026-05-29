package com.campustrade.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campustrade.dto.GoodsDTO;
import com.campustrade.entity.Goods;

public interface GoodsService extends IService<Goods> {
    /** 发布商品 */
    Goods publish(GoodsDTO dto, Long userId);
    /** 分页查询在售商品 */
    Page<Goods> pageList(Integer page, Integer size, String category, String keyword);
    /** 商品详情（增加浏览量） */
    Goods detail(Long id);
    /** 删除商品 */
    void deleteGoods(Long id, Long userId);
    /** 我的商品 */
    Page<Goods> myGoods(Integer page, Integer size, Long userId);
    /** 审核商品 */
    void audit(Long id, Integer status);
}
