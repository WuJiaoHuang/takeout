package com.sky.controller.admin;


import com.github.pagehelper.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController("AdminSetmealController")
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache",key="#setmealDTO.categoryId")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO){
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result selectPage(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询:{}",setmealPageQueryDTO);
        PageResult result = setmealService.selectPage(setmealPageQueryDTO);
        return Result.success(result);
    }

    @PutMapping
    @ApiOperation(("修改套餐"))
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("更新信息为:{}",setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result selectById(@PathVariable Long id){
        log.info("需查询的id:{}",id);
        SetmealVO setmealVO=  setmealService.selectById(id);
        return Result.success(setmealVO);
    }


    @PostMapping("/status/{status}")
    @ApiOperation("更改套餐销售状态")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result updateStatus(@PathVariable Integer status,Long id){
        log.info("需要更改的id:{} 需要更改的状态:{} ",id,status);
        setmealService.updateStatus(id,status);
        return Result.success();
    }


    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result deleteById(@RequestParam List<Long> ids){

        setmealService.deleteById(ids);
        return Result.success();
    }

}
