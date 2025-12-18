package com.red.education.module.points.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.red.education.common.result.Result;
import com.red.education.module.points.dto.ExchangeDTO;
import com.red.education.module.points.dto.ProductQueryDTO;
import com.red.education.module.points.entity.ExchangeRecord;
import com.red.education.module.points.entity.PointRecord;
import com.red.education.module.points.entity.Product;
import com.red.education.module.points.service.ExchangeService;
import com.red.education.module.points.service.PointService;
import com.red.education.module.points.service.ProductService;
import com.red.education.module.points.vo.ExchangeRecordVO;
import com.red.education.module.points.vo.PointLeaderboardVO;
import com.red.education.module.points.vo.ProductVO;
import com.red.education.module.user.entity.User;
import com.red.education.module.user.mapper.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分Controller
 */
@Api(tags = "积分管理")
@RestController
@RequestMapping("/points")
public class PointController {

    @Autowired
    private PointService pointService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ExchangeService exchangeService;

    @Autowired
    private UserMapper userMapper;

    // ============= 积分商品相关接口 =============

    @ApiOperation("获取积分商品列表")
    @GetMapping("/goods")
    public Result<Page<ProductVO>> getGoods(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword) {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setStatus(1); // 只显示上架商品
        queryDTO.setType(type);
        queryDTO.setKeyword(keyword);

        Page<ProductVO> result = productService.listProducts(page, pageSize, queryDTO);
        return Result.success(result);
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/goods/{id}")
    public Result<Product> getGoodsDetail(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return Result.success(product);
    }

    // ============= 积分兑换相关接口 =============

    @ApiOperation("兑换商品")
    @PostMapping("/exchange")
    public Result<ExchangeRecord> exchangeGoods(
            Authentication authentication,
            @Valid @RequestBody ExchangeDTO exchangeDTO) {
        Long userId = (Long) authentication.getPrincipal();
        ExchangeRecord record = exchangeService.exchangeProduct(userId, exchangeDTO);
        return Result.success(record);
    }

    @ApiOperation("获取兑换记录")
    @GetMapping("/exchange/records")
    public Result<Page<ExchangeRecordVO>> getExchangeRecords(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = (Long) authentication.getPrincipal();
        Page<ExchangeRecordVO> page = exchangeService.getMyExchangeRecords(userId, current, size);
        return Result.success(page);
    }

    // ============= 积分余额和明细相关接口 =============

    @ApiOperation("获取积分余额")
    @GetMapping("/balance")
    public Result<Map<String, Object>> getPointsBalance(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = userMapper.selectById(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("points", user != null ? user.getPoints() : 0);
        result.put("username", user != null ? user.getUsername() : "");
        result.put("nickname", user != null ? user.getNickname() : "");

        return Result.success(result);
    }

    @ApiOperation("获取积分明细")
    @GetMapping("/history")
    public Result<Page<PointRecord>> getPointsHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type) {
        Long userId = (Long) authentication.getPrincipal();
        Page<PointRecord> page = pointService.getPointRecords(userId, current, size, type);
        return Result.success(page);
    }

    // ============= 积分排行榜接口 =============

    @ApiOperation("获取积分排行榜")
    @GetMapping("/leaderboard")
    public Result<List<PointLeaderboardVO>> getLeaderboard(
            @RequestParam(defaultValue = "10") Integer topN) {
        List<PointLeaderboardVO> leaderboard = pointService.getLeaderboard(topN);
        // 设置排名
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }
        return Result.success(leaderboard);
    }

    @ApiOperation("获取积分排行榜（别名）")
    @GetMapping("/ranking")
    public Result<Map<String, Object>> getPointsRanking(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "50") Integer pageSize) {
        List<PointLeaderboardVO> leaderboard = pointService.getLeaderboard(pageSize);
        // 设置排名和用户ID
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("records", leaderboard);
        result.put("total", leaderboard.size());
        result.put("current", page);
        result.put("size", pageSize);
        return Result.success(result);
    }
}
