package com.sky.controller.user;


import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


@Slf4j
@RestController("UserSetmealController")
@RequestMapping("/user/setmeal")
@Api("用户端套餐浏览接口")
public class SetmealController{
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId")
    public Result selectBycategoryId(Long categoryId){
        log.info("根据categoryId {} 查询套餐",categoryId);
        List<Setmeal> setmeal = setmealService.selectBycategoryId(categoryId);
        return Result.success(setmeal);
    }


    @ApiOperation("根据套餐id查询包含的菜品")
    @GetMapping("/dish/{id}")
    public Result selectDishBySetmealId(Long id){
        log.info("根据套餐id查询包含的菜品:{}",id);
        List<DishItemVO> result = setmealService.selectDishBySetmealId(id);
        return Result.success(result);
    }
}


