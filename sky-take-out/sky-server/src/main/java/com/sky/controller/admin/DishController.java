package com.sky.controller.admin;


import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//菜品管理
@RestController("adminDishController")
@RequestMapping("/admin/dish")
@Api("菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DishService dishService;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.saveWithFloavor(dishDTO);
        //清理缓存数据
        String key = "dish"+dishDTO.getCategoryId();
        redisTemplate.delete(key);

        return Result.success();
    }


    @GetMapping("/page")
    public Result listByPage(DishPageQueryDTO q){
        log.info("菜品分页查询:{}",q);
        PageResult p= dishService.listByPage(q);
        return Result.success(p);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除:{}",ids);
        Set keys =  redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品:{}",id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }


    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);

        Set keys =  redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return Result.success();
    }


    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售，停售")
    public Result updateStatus(@PathVariable Integer status,Long id){
        log.info("id:{} 修改菜品:{}",id,status);

        Set keys =  redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        dishService.updateStatus(status,id);
        return Result.success();
    }


    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result getByCategoryId(Long categoryId){
        List<Dish> dishes = dishService.getByCategoryId(categoryId);
        return Result.success(dishes);


    }

}
