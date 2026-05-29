package com.campustrade.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campustrade.common.Result;
import com.campustrade.dto.GoodsDTO;
import com.campustrade.entity.Goods;
import com.campustrade.service.GoodsService;
import com.campustrade.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    private final GoodsService goodsService;
    private final JwtUtil jwtUtil;

    public GoodsController(GoodsService goodsService, JwtUtil jwtUtil) {
        this.goodsService = goodsService;
        this.jwtUtil = jwtUtil;
    }

    /** 发布商品 */
    @PostMapping
    public Result<Goods> publish(@RequestHeader("Authorization") String token, @Valid @RequestBody GoodsDTO dto) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.ok(goodsService.publish(dto, userId));
    }

    /** 商品列表 */
    @GetMapping
    public Result<Page<Goods>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return Result.ok(goodsService.pageList(page, size, category, keyword));
    }

    /** 商品详情 */
    @GetMapping("/{id}")
    public Result<Goods> detail(@PathVariable Long id) {
        return Result.ok(goodsService.detail(id));
    }

    /** 删除商品 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        goodsService.deleteGoods(id, userId);
        return Result.ok();
    }

    /** 我的商品 */
    @GetMapping("/my")
    public Result<Page<Goods>> myGoods(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = jwtUtil.getUserId(token.replace("Bearer ", ""));
        return Result.ok(goodsService.myGoods(page, size, userId));
    }

    /** 搜索商品 */
    @GetMapping("/search")
    public Result<Page<Goods>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(goodsService.pageList(page, size, null, keyword));
    }
}
